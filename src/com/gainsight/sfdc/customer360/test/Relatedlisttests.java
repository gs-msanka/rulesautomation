package com.gainsight.sfdc.customer360.test;

import com.gainsight.sfdc.tests.BaseTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class Relatedlisttests extends BaseTest {
    @BeforeClass
    public void setUp() {
        basepage.login();
    }

   /* @Test
    public void goto360Page() throws InterruptedException {
        //basepage.setDefaultApplication("Gainsight");
        CustomerSuccess360 c360Tab = basepage.clickOnCust360Tab();

        Customer360Page c360Page = c360Tab.searchCustomer("Abacus Programming Corporation", true);
        Relatedlist as = new Relatedlist("Opportunities");
        //as.getColHeaders("Contacts");
       // as.getTableDataJSON("Opportunities");
        boolean a = c360Tab.verifySectionisDisplayed("Opportunities");
        //boolean b = c360Tab.isOrderOfSectionMaintained("Summary | Scorecard | Attributes | Transactions");
        Assert.assertTrue(a);
        //Assert.assertTrue(b);

    }
*/


    @AfterClass
    public void tearDown(){
        basepage.logout();
    }
}
