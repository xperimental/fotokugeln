package net.sourcewalker.kugeln;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

public class PersistenceManagerFactory {

    private static final javax.jdo.PersistenceManagerFactory pmfInstance = JDOHelper
            .getPersistenceManagerFactory("transactions-optional");

    private PersistenceManagerFactory() {
    }

    public static PersistenceManager get() {
        return pmfInstance.getPersistenceManager();
    }

}
