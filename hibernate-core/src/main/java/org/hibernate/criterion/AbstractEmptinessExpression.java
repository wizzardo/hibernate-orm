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
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.sql.ConditionFragment;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

/**
 * Implementation of AbstractEmptinessExpression.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractEmptinessExpression implements Criterion {

	private static final TypedValue[] NO_VALUES = new TypedValue[0];

	protected final String propertyName;

	protected AbstractEmptinessExpression(String propertyName) {
		this.propertyName = propertyName;
	}

	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		StringBuilder sb = new StringBuilder();
		toSqlString(criteria, criteriaQuery, sb);
		return sb.toString();
	}

	protected abstract boolean excludeEmpty();

	public final void toSqlString(Criteria criteria, CriteriaQuery criteriaQuery, StringBuilder sb) throws HibernateException {
		String entityName = criteriaQuery.getEntityName(criteria, propertyName);
		String actualPropertyName = criteriaQuery.getPropertyName(propertyName);
		String sqlAlias = criteriaQuery.getSQLAlias(criteria, propertyName);

		SessionFactoryImplementor factory = criteriaQuery.getFactory();
		QueryableCollection collectionPersister = getQueryableCollection(entityName, actualPropertyName, factory);

		String[] collectionKeys = collectionPersister.getKeyColumnNames();
		String[] ownerKeys = ( ( Loadable ) factory.getEntityPersister( entityName ) ).getIdentifierColumnNames();

		if (excludeEmpty())
			sb.append("exists ");
		else
			sb.append("not exists ");

		sb.append("(select 1 from ")
				.append(collectionPersister.getTableName())
				.append(" where ");
		new ConditionFragment().setTableAlias(sqlAlias).setCondition(ownerKeys, collectionKeys).toFragmentString(sb);
		sb.append(")");
	}


	protected QueryableCollection getQueryableCollection(String entityName, String propertyName, SessionFactoryImplementor factory)
	        throws HibernateException {
		PropertyMapping ownerMapping = ( PropertyMapping ) factory.getEntityPersister( entityName );
		Type type = ownerMapping.toType( propertyName );
		if ( !type.isCollectionType() ) {
			throw new MappingException(
			        "Property path [" + entityName + "." + propertyName + "] does not reference a collection"
			);
		}

		String role = ( ( CollectionType ) type ).getRole();
		try {
			return ( QueryableCollection ) factory.getCollectionPersister( role );
		}
		catch ( ClassCastException cce ) {
			throw new QueryException( "collection role is not queryable: " + role );
		}
		catch ( Exception e ) {
			throw new QueryException( "collection role not found: " + role );
		}
	}

	public final TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
	        throws HibernateException {
		return NO_VALUES;
	}

	public final String toString() {
		return propertyName + ( excludeEmpty() ? " is not empty" : " is empty" );
	}
}
