/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
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
 *
 */
package org.hibernate.criterion;


import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.engine.TypedValue;
import org.hibernate.util.StringHelper;

/**
 * Constrains a property to be null
 * @author Gavin King
 */
public class NullExpression implements Criterion {

	private final String propertyName;

	private static final TypedValue[] NO_VALUES = new TypedValue[0];

	protected NullExpression(String propertyName) {
		this.propertyName = propertyName;
	}

	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		StringBuilder sb = new StringBuilder();
		toSqlString(criteria, criteriaQuery, sb);
		return sb.toString();
	}

	public void toSqlString(Criteria criteria, CriteriaQuery criteriaQuery, StringBuilder builder)
			throws HibernateException {
		String[] columns = criteriaQuery.findColumns(propertyName, criteria);
		if (columns.length > 1)
			builder.append('(');

		StringHelper.join(
				" and ",
				StringHelper.suffix(columns, " is null"),
				builder
		);
		if (columns.length > 1)
			builder.append(')');

		//TODO: get SQL rendering out of this package!
	}

	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
	throws HibernateException {
		return NO_VALUES;
	}

	public String toString() {
		return propertyName + " is null";
	}

}
