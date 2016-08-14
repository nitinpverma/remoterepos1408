package com.csc.fsg.life.xg.utils.legacy;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.csc.fsg.life.dao.model.DAOModelCollection;
import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.utils.dbm.dao.XGFUNCDAO;
import com.csc.fsg.life.xg.utils.dbm.dao.XGRULEDAO;
import com.csc.fsg.life.xg.utils.dbm.model.XGFUNCModel;
import com.csc.fsg.life.xg.utils.dbm.model.XGRULEModel;

public class CrossDBExprFix {

	public static void main(String[] args) throws SQLException, Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("config/xmlg-config.xml");
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) context.getBean("xmlgConfig"));
		EnvironmentInitializer.setServerDocRoot("D:/Projects/XGBase/lifewsWeb/webApplication");
		EnvironmentInitializer.init(null);

		String sourceEnv = "BASE";
		String targetEnv = "NBA59";

		XGRULEDAO baseRuleDao = new XGRULEDAO(sourceEnv);
		DAOModelCollection<XGRULEModel> ruleList = baseRuleDao.getList();

		XGRULEDAO nbaRuleDao = new XGRULEDAO(targetEnv);
		DAOModelCollection<XGRULEModel> nbaRuleList = nbaRuleDao.getList();

		System.out.println("Total Rule count : " + nbaRuleList.size());
		for (int i = 0; i < nbaRuleList.size(); i++) {

			XGRULEModel nbaModel = (XGRULEModel) nbaRuleList.get(i);
			for (int j = 0; j < ruleList.size(); j++) {
				XGRULEModel baseModel = (XGRULEModel) ruleList.get(j);
				if (nbaModel.getLabel().equalsIgnoreCase(baseModel.getLabel())) {
					nbaModel.setExpressClass(baseModel.getExpressClass());
					int k = nbaRuleDao.update(nbaModel);
					if (k <= 0) {
						System.out.println(i + " *** Rule Errored on: "
								+ nbaModel.getLabel());
					} else {
						System.out.println(i + " Rule Successful : "
								+ nbaModel.getLabel());
					}
					break;
				}
			}
		}
		XGFUNCDAO baseFuncdao = new XGFUNCDAO(sourceEnv);
		DAOModelCollection funcList = baseFuncdao.getList();

		XGFUNCDAO nbaFuncdao = new XGFUNCDAO(targetEnv);
		DAOModelCollection nbaFuncList = nbaFuncdao.getList();

		System.out.println("Total Function count : " + nbaFuncList.size());
		for (int i = 0; i < nbaFuncList.size(); i++) {

			XGFUNCModel nbaModel = (XGFUNCModel) nbaFuncList.get(i);
			for (int j = 0; j < funcList.size(); j++) {
				XGFUNCModel baseModel = (XGFUNCModel) funcList.get(j);
				if (nbaModel.getLabel().equalsIgnoreCase(baseModel.getLabel())) {
					nbaModel.setExpressionClass(baseModel.getExpressionClass());
					int k = nbaFuncdao.update(nbaModel);
					if (k <= 0) {
						System.out.println(i + " *** Func Errored on: "
								+ nbaModel.getLabel());
					} else {
						System.out.println(i + " Func Successful : "
								+ nbaModel.getLabel());
					}
					break;
				}
			}
		}
		System.out.println("Update Successful.");
	}
}
