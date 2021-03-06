/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
package org.hibernate.boot.archive.scan.internal;

import org.hibernate.boot.archive.scan.spi.ScanOptions;

/**
 * @author Steve Ebersole
 */
public class StandardScanOptions implements ScanOptions {
	private final boolean detectClassesInRoot;
	private final boolean detectClassesInNonRoot;
	private final boolean detectHibernateMappingFiles;

	public StandardScanOptions() {
		this( "hbm,class", false );
	}

	public StandardScanOptions(String explicitDetectionSetting, boolean persistenceUnitExcludeUnlistedClassesValue) {
		if ( explicitDetectionSetting == null ) {
			detectHibernateMappingFiles = true;
			detectClassesInRoot = ! persistenceUnitExcludeUnlistedClassesValue;
			detectClassesInNonRoot = true;
		}
		else {
			detectHibernateMappingFiles = explicitDetectionSetting.contains( "hbm" );
			detectClassesInRoot = explicitDetectionSetting.contains( "class" );
			detectClassesInNonRoot = detectClassesInRoot;
		}
	}

	@Override
	public boolean canDetectUnlistedClassesInRoot() {
		return detectClassesInRoot;
	}

	@Override
	public boolean canDetectUnlistedClassesInNonRoot() {
		return detectClassesInNonRoot;
	}

	@Override
	public boolean canDetectHibernateMappingFiles() {
		return detectHibernateMappingFiles;
	}
}
