package com.becomejavasenior.template.impl;

import com.becomejavasenior.entity.*;
import com.becomejavasenior.template.DealDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-template.xml")
public class JdbcDealDAOImplTest {

    private static final String DEFAULT_NAME = "Default Name";
    private static final Date DEFAULT_DATE = new Timestamp(new Date().getTime());
    private static User userForDealTest;
    private static Stage stageForDealTest;
    private static Company companyForDealTest;
    private static int dealTestId;

// TODO: bug! makes records in deal table, but not removes them after testing
//    @Autowired
    private DealDAO dealDAO;

    @BeforeClass
    public static void init() {
        userForDealTest = new User();
        userForDealTest.setId(1);
        stageForDealTest = new Stage();
        stageForDealTest.setId(1);
        companyForDealTest = new Company();
        companyForDealTest.setId(1);
    }

    @Before
    public void setUp() {
        dealTestId = 0;
    }

    @Test
    @Rollback(true)
    public void testUpdate() throws SQLException {
        String updatedName = "Updated Name";
        Date updatedCreateDate = new Timestamp(1L << 41);
        User updatedUser = new User();
        updatedUser.setId(2);
        Company updatedCompany = new Company();
        updatedCompany.setId(2);
        Stage updatedStage = new Stage();
        updatedStage.setId(2);
        Contact updatedContact = new Contact();
        updatedContact.setId(1);

        Deal dealTest = new Deal();
        dealTest.setName(DEFAULT_NAME);
        dealTest.setDateCreate(DEFAULT_DATE);
        dealTest.setCreator(userForDealTest);
        dealTest.setCompany(companyForDealTest);
        dealTest.setStage(stageForDealTest);
        dealTestId = dealDAO.insert(dealTest);
        Assert.assertNotNull("Deal before update must not be null", dealTest);

        dealTest.setId(dealTestId);
        dealTest.setName(updatedName);
        dealTest.setDateCreate(updatedCreateDate);
        dealTest.setCreator(updatedUser);
        dealTest.setResponsibleUser(updatedUser);
        dealTest.setCompany(updatedCompany);
        dealTest.setStage(updatedStage);
        dealTest.setAmount(BigDecimal.valueOf(500.55));
        dealTest.setPrimaryContact(updatedContact);

        dealDAO.update(dealTest);

        Deal updatedDeal = dealDAO.getById(dealTestId);
        Assert.assertNotNull("Deal after update is null", updatedDeal);
        Assert.assertEquals("Deal name update failed", updatedName, updatedDeal.getName());
        Assert.assertEquals("Date of deal creation update failed", updatedCreateDate, updatedDeal.getDateCreate());
        Assert.assertEquals("Deal creator update failed", updatedUser.getId(), updatedDeal.getCreator().getId());
        Assert.assertEquals("Deal responsible user update failed", updatedUser.getId(), updatedDeal.getResponsibleUser().getId());
        Assert.assertEquals("Deal link to Company update failed", updatedCompany.getId(), updatedDeal.getCompany().getId());
        Assert.assertEquals("Deal link to Stage update failed", updatedStage.getId(), updatedDeal.getStage().getId());
        Assert.assertEquals("getAmount: ", BigDecimal.valueOf(500.55), updatedDeal.getAmount());
        Assert.assertEquals("Deal link to Primary Contact update failed", updatedContact.getId(), updatedDeal.getPrimaryContact().getId());


    }

    @Test
    @Rollback(true)
    public void testCreate() {
        Deal dealTest = new Deal();
        Assert.assertTrue("Deal ID before creation must be '0'", dealTest.getId() == 0);

        try {
            dealTestId = dealDAO.insert(dealTest);
        } catch (Exception e) {
            Assert.assertTrue("Empty deal ID must be '0'", dealTest.getId() == 0);
        } finally {
            Assert.assertTrue("Empty deal ID must be '0'", dealTestId == 0);
        }

        dealTest.setName(DEFAULT_NAME);
        dealTest.setStage(stageForDealTest);
        dealTest.setCompany(companyForDealTest);
        dealTest.setDateCreate(DEFAULT_DATE);
        dealTest.setCreator(userForDealTest);
        dealTestId = dealDAO.insert(dealTest);

        Assert.assertNotNull("Deal creation failed", dealTest);
        Assert.assertTrue("Deal ID after creation must be not '0'", dealTestId > 0);
        Assert.assertNotNull("Deal date of creation must be not null", dealTest.getDateCreate());
        Assert.assertNotNull("Deal creator must be not null", dealTest.getCreator());

    }

    @Test
    public void testGetByPK() {
        Assert.assertNotNull("Deal read by PK failed", dealDAO.getById(1));
    }

    @Test
    @Rollback(true)
    public void testDelete() {
        Deal dealTest = new Deal();
        dealTest.setName(DEFAULT_NAME);
        dealTest.setDateCreate(DEFAULT_DATE);
        dealTest.setCreator(userForDealTest);
        dealTest.setCompany(companyForDealTest);
        dealTest.setStage(stageForDealTest);
        dealTestId = dealDAO.insert(dealTest);

        List dealList = dealDAO.getAll();
        int oldListSize = dealList.size();
        Assert.assertTrue("Deal list must not be empty", oldListSize > 0);

    }

    @Test
    public void testGetAll() {
        List dealList = dealDAO.getAll();
        Assert.assertNotNull("Deal list must not be null", dealList);
        Assert.assertTrue("Deal list must not be empty", dealList.size() > 0);
    }


    @Test
    public void testGetDealsForList() throws Exception {
        List dealsForList = new ArrayList<>();
        Assert.assertEquals("Deals for list must be ArrayList[]", new ArrayList(), dealsForList);

        dealsForList = dealDAO.getDealsForList();
        Assert.assertNotNull("Deals for list must not be null", dealsForList);
        Assert.assertFalse("Deals for list must not be null", dealsForList.contains(""));
        Assert.assertTrue("Deal for list must not be empty", dealsForList.size() > 0);
    }

    @Test
    public void testGetDealsByStage() throws Exception {
        List dealsStage = dealDAO.getDealsByStage("Принимают решение");
        Assert.assertNotNull("Deals for list must not be null", dealsStage);
    }

    @Test
    public void testGetContactsByDealName() throws Exception {
        List contacts = dealDAO.getContactsByDealId(1);

        Assert.assertNotNull(contacts.size());
        Assert.assertTrue(contacts.size() > 0);
    }

    @Test
    public void testGetAllStage() throws Exception {
        List stages = dealDAO.getAllStage();
        Assert.assertNotNull(stages.size());
        Assert.assertTrue(stages.size() > 0);

    }

    @Test
    public void testGetStageDealsList() throws Exception {
        Map stages = dealDAO.getStageDealsList();
        Assert.assertNotNull(stages.size());
        Assert.assertTrue(stages.size() > 0);
    }

}