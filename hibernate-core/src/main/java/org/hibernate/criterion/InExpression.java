/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.criterion;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

import java.util.ArrayList;

/**
 * Constrains the property to a specified list of values
 * @author Gavin King
 */
public class InExpression implements Criterion {

	private final String propertyName;
	private final Object[] values;

	protected InExpression(String propertyName, Object[] values) {
		this.propertyName = propertyName;
		this.values = values;
	}

	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		StringBuilder sb = new StringBuilder();
		toSqlString(criteria, criteriaQuery, sb);
		return sb.toString();
	}

    public void toSqlString( Criteria criteria, CriteriaQuery criteriaQuery, StringBuilder builder)
            throws HibernateException {
        String[] columns = criteriaQuery.findColumns(propertyName, criteria);
        if ( criteriaQuery.getFactory().getDialect()
                .supportsRowValueConstructorSyntaxInInList() || columns.length<=1) {

            String singleValueParam = StringHelper.repeat( "?, ",
                    columns.length - 1 )
                    + "?";
            if ( columns.length > 1 )
                singleValueParam = '(' + singleValueParam + ')';

			if (columns.length > 1)
				builder.append('(');

			StringHelper.join(", ", columns, builder);

			if (columns.length > 1)
				builder.append(')');

			builder.append(" in (");
			if (values.length > 0) {
				StringHelper.repeat(singleValueParam + ", ", values.length - 1, builder);
				builder.append(singleValueParam);
			}
			builder.append(')');
		} else {
           String cols = " ( " + StringHelper.join( " = ? and ", columns ) + "= ? ) ";
			builder.append(" ( ");
			if(values.length>0){
				StringHelper.repeat(cols + "or ", values.length - 1, builder);
				builder.append(cols);
			}
			builder.append(" ) ");

        }
    }

	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
	throws HibernateException {
		ArrayList list = new ArrayList();
		Type type = criteriaQuery.getTypeUsingProjection(criteria, propertyName);
		if ( type.isComponentType() ) {
			CompositeType actype = (CompositeType) type;
			Type[] types = actype.getSubtypes();
			for ( int j=0; j<values.length; j++ ) {
				for ( int i=0; i<types.length; i++ ) {
					Object subval = values[j]==null ? 
						null : 
						actype.getPropertyValues( values[j], EntityMode.POJO )[i];
					list.add( new TypedValue( types[i], subval, EntityMode.POJO ) );
				}
			}
		}
		else {
			for ( int j=0; j<values.length; j++ ) {
				list.add( new TypedValue( type, values[j], EntityMode.POJO ) );
			}
		}
		return (TypedValue[]) list.toArray( new TypedValue[ list.size() ] );
	}

	public String toString() {
		return propertyName + " in (" + StringHelper.toString(values) + ')';
	}

}
