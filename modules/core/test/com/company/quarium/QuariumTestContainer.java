package com.company.quarium;

import com.haulmont.cuba.testsupport.TestContainer;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Arrays;

public class QuariumTestContainer extends TestContainer {

    public QuariumTestContainer() {
        super();
        //noinspection ArraysAsListWithZeroOrOneArgument
        appComponents = Arrays.asList(
                "com.haulmont.cuba",
                "com.haulmont.addon.restapi",
                "com.haulmont.reports",
                "com.haulmont.addon.ldap",
                "com.haulmont.fts",
                "com.haulmont.charts",
                "com.haulmont.addon.helium");
        appPropertiesFiles = Arrays.asList(
                // List the files defined in your web.xml
                // in appPropertiesConfig context parameter of the core module
                "com/company/quarium/app.properties",
                // Add this file which is located in CUBA and defines some properties
                // specifically for test environment. You can replace it with your own
                // or add another one in the end.
                "com/company/quarium/test-app.properties");
        autoConfigureDataSource();
    }

    

    public static class Common extends QuariumTestContainer {

        public static final QuariumTestContainer.Common INSTANCE = new QuariumTestContainer.Common();

        private static volatile boolean initialized;

        private Common() {
        }

        @Override
        public void beforeAll(ExtensionContext extensionContext) throws Exception {
            if (!initialized) {
                super.beforeAll(extensionContext);
                initialized = true;
            }
            setupContext();
        }
        

        @SuppressWarnings("RedundantThrows")
        @Override
        public void afterAll(ExtensionContext extensionContext) throws Exception {
            cleanupContext();
            // never stops - do not call super
        }
        
    }
}