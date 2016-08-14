/*
 * Modifications: Technical Enhancement #T0092
 */
package com.csc.fsg.life.xg.utils;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.csc.fsg.life.biz.exception.BusinessException;
import com.csc.fsg.life.biz.service.ServiceParam;
import com.csc.fsg.life.xg.common.log.LogConstants;
import com.csc.fsg.life.xg.sm.arch.XgCopyobjectService;
import com.csc.fsg.life.xg.ws.ControllerBean;

/**
 * Web service wrapper tester. It needs little bit tweaking before it can be used.
 */
public class TestClass extends XgCopyobjectService {

	private static final Log log = LogFactory.getLog(LogConstants.SERVER_LOG);

	/**
	 * The main method.
	 * 
	 * @param argv the arguments
	 * @throws Exception the exception
	 */
	public static void main(String argv[]) throws Exception {
		try {
			TestClass service = new TestClass();
			// service.testAllocationInq();
			// service.testContractProfile();
			// service.testBilling();
			// service.testDollarCostAve();
			// service.testPolicyValues();
			// service.testProducerProfile();
			// service.testRebalance();
			// service.getContractDetails();

		} catch (Throwable e) {
			log.error("", e);
		}
	}

	// private void getContractDetails() throws CopyObjectException,
	// LifeException, BusinessException, SQLException {
	//
	// log.info("Contract Details Service start.....");
	//
	// UserContext userContext =
	// null;//CopyobjectWebService.buildUserContext(null);
	// ServiceParam param = null;//buildServiceParam(userContext,null);
	// AnnuityObject annuityObject = new AnnuityObject();
	// InquiryRequestCopyObject inqReqCO = new InquiryRequestCopyObject();
	// inqReqCO.init(userContext);
	// inqReqCO.setCompanyCode("EQC");
	// inqReqCO.setMasterId("505000501");
	// inqReqCO.setProductCode("A2");
	// annuityObject.setRequestObj(inqReqCO);
	//
	// Object object = null;//annuityObject.service(null, null);
	// log.info(object);
	// log.info("Contract Details Information Service end");
	//
	//
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csc.fsg.life.wma.CopyobjectService#getDefaultTrx()
	 */
	/**
	 * Gets the default trx.
	 * 
	 * @return the default trx
	 */
	public String getDefaultTrx() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.csc.fsg.life.wma.CopyobjectService#internalService(com.csc.fsg.life
	 * .webservices.ControllerBean, java.lang.String)
	 */
	/**
	 * Internal service.
	 * 
	 * @param controllerBean the controller bean
	 * @param databaseschema the databaseschema
	 * @return the object
	 * @throws SQLException the sQL exception
	 * @throws BusinessException the business exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Object internalService(ControllerBean controllerBean,
			String databaseschema) throws SQLException, BusinessException,
			IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.csc.fsg.life.biz.service.ServiceManagerObjectService#internalService(com.csc.fsg.life.biz.service.ServiceParam)
	 */
	public Object internalService(ServiceParam serviceParam)
			throws BusinessException {
		return null;
	}

	/**
	 * Test method for ProducerProfile WMA copyobject service.
	 * 
	 * @throws Exception
	 */
	// public void testProducerProfile() throws Exception {
	//
	// log.info("Producer Profile Service start.....");
	//
	// UserContext userContext = CopyobjectService.buildUserContext();
	// ServiceParam param = buildServiceParam(userContext);
	//
	// AnnProducerProfileServiceImpl prodProfService =
	// (AnnProducerProfileServiceImpl)
	// _beanFactory.getBean("annProducerProfileService");
	// ProducerProfile prodProfCO = new ProducerProfileCopyObject();
	// prodProfCO.init(userContext);
	// prodProfCO.setCompanyCode("VCS");
	// prodProfCO.setMasterId("DEATH1");
	// prodProfCO.setProductCode("A1");
	//
	// PolicySummaryImpl ps = new PolicySummaryImpl();
	// ps.setCompanyCode(prodProfCO.getCompanyCode());
	// ps.setMasterId(prodProfCO.getMasterId());
	// ps.setProductCode("A1");
	// ((WMAUserContext) userContext).setPolicySummary(ps);
	//
	// prodProfCO = prodProfService.inquire(param, prodProfCO);
	// log.info("" + prodProfCO);
	// log.info("Producer Profile Service end");
	// }
	//
	// /**
	// * Test method for Rebalance WMA copyobject service.
	// *
	// * @throws Exception
	// */
	// public void testRebalance() throws Exception {
	// log.info("Rebalance Service start.....");
	//
	// UserContext userContext = CopyobjectService.buildUserContext();
	// ServiceParam param = buildServiceParam(userContext);
	//
	// AnnRebalanceUpdateService ruService = (AnnRebalanceUpdateService)
	// _beanFactory.getBean("annRebalanceUpdateService");
	// RebalanceUpdate ruCopybook = new RebalanceUpdateCopyObject();
	// ruCopybook.init(userContext);
	// ruCopybook.setCompanyCode("VCS");
	// ruCopybook.setMasterId("DEATH1");
	// ruCopybook.setProductCode("A1");
	// // ruCopybook.setTrxCode("LCI1");
	// PolicySummaryImpl ps = new PolicySummaryImpl();
	// ps.setCompanyCode(ruCopybook.getCompanyCode());
	// ps.setMasterId(ruCopybook.getMasterId());
	// ps.setProductCode("A1");
	// ((WMAUserContext) userContext).setPolicySummary(ps);
	//
	// ruCopybook = ruService.inquire(param, ruCopybook);
	//
	// log.info("" + ruCopybook);
	// log.info("Rebalance Service end");
	// }
	//
	// /**
	// * Test method for DollarCostAve WMA copyobject service.
	// *
	// * @throws Exception
	// */
	// public void testDollarCostAve() throws Exception {
	//
	// log.info("DollarCostAve Service start.....");
	//
	// InquiryChangeRequestCopyObject requestObj = new
	// InquiryChangeRequestCopyObject();
	// UserContext userContext = CopyobjectService.buildUserContext();
	// requestObj.init(userContext);
	// requestObj.setCompanyCode("VCS");
	// requestObj.setMasterId("DEATH1");
	// requestObj.setProductCode("A1");
	// requestObj.setTrxCode("LGCI1");
	//
	// ServiceParam param = buildServiceParam(userContext);
	// AnnDcaUpdateService dcaService = (AnnDcaUpdateService)
	// _beanFactory.getBean("annDcaUpdateService");
	// DcaUpdate dcaUpdateCO = null;
	// dcaUpdateCO = dcaService.inquire(param, requestObj);
	// log.info("" + dcaUpdateCO);
	// log.info("DollarCostAve Service end");
	// }
	//
	// /**
	// * Test method for PolicyValues WMA copyobject service.
	// *
	// * @throws Exception
	// */
	// public void testPolicyValues() throws Exception {
	//
	// log.info("Policy Values Service start.....");
	// UserContext userContext = CopyobjectService.buildUserContext();
	// ServiceParam param = buildServiceParam(userContext);
	//
	// AnnValuesService valuesService = (AnnValuesService)
	// _beanFactory.getBean("annValuesService");
	// Values valuesCO = new ValuesCopyObject();
	// InquiryRequest inquireReqCO = new InquiryRequestCopyObject();
	// inquireReqCO.init(userContext);
	// inquireReqCO.setCompanyCode("VCS");
	// inquireReqCO.setMasterId("DEATH1");
	// inquireReqCO.setProductCode("A1");
	// valuesCO = valuesService.performValuation(param, inquireReqCO);
	//
	// log.info("" + valuesCO);
	// log.info("Policy Values Service end");
	// }
	//
	// /**
	// * Test method for Billing WMA copyobject service.
	// *
	// * @throws Exception
	// */
	// public void testBilling() throws Exception {
	//
	// log.info("Billing Service start.....");
	// InquiryChangeRequestCopyObject requestObj = new
	// InquiryChangeRequestCopyObject();
	// UserContext userContext = CopyobjectService.buildUserContext();
	// requestObj.init(userContext);
	// requestObj.setCompanyCode("VCS");
	// requestObj.setMasterId("DEATH1");
	// requestObj.setProductCode("A1");
	//
	// ServiceParam param = buildServiceParam(userContext);
	// AnnBillingUpdateService billingService = (AnnBillingUpdateService)
	// _beanFactory.getBean("annBillingUpdateService");
	// // BillingUpdate billingCO = new BillingUpdateCopyObject();
	// Object billingCO = billingService.fetchBilling(param, requestObj);
	//
	// log.info("" + billingCO);
	// log.info("Billing Service end");
	// }
	//
	// /**
	// * Test method for ContractProfileAdmin WMA copyobject service.
	// *
	// * @throws Exception
	// */
	// public void testContractProfile() throws Exception {
	//
	// log.info("Contract Profile Service start.....");
	// ContractProfileAdminCopyObject outCopyObj = new
	// ContractProfileAdminCopyObject();
	// InquiryChangeRequestCopyObject copyObj = new
	// InquiryChangeRequestCopyObject();
	// UserContext userContext = CopyobjectService.buildUserContext();
	// copyObj.init(userContext);
	// copyObj.setCompanyCode("EQC");
	// copyObj.setMasterId("505000501");
	// copyObj.setProductCode("A2");
	// copyObj.setTrxCode("LCI1");
	//
	// ServiceParam param = buildServiceParam(userContext);
	// // PolicySummaryImpl ps = new PolicySummaryImpl();
	// // ps.setCompanyCode(copyObj.getCompanyCode());
	// // ps.setMasterId(copyObj.getMasterId());
	// // ps.setProductCode("A1");
	// // ((WMAUserContext) userContext).setPolicySummary(ps);
	//
	// // get the Values service from spring's bean factory
	// AnnContractProfileAdminService serviceImpl =
	// (AnnContractProfileAdminService)
	// _beanFactory.getBean("annContractProfileAdminService");
	// outCopyObj = (ContractProfileAdminCopyObject) serviceImpl.inquire(param,
	// copyObj);
	//
	// log.info("" + outCopyObj);
	// log.info("Contract Profile Service end");
	// }
}
