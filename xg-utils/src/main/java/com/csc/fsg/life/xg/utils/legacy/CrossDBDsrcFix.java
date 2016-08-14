package com.csc.fsg.life.xg.utils.legacy;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.csc.fsg.life.dao.model.DAOModelCollection;
import com.csc.fsg.life.xg.db.ApplicationConfig;
import com.csc.fsg.life.xg.db.EnvironmentFactory;
import com.csc.fsg.life.xg.servlet.EnvironmentInitializer;
import com.csc.fsg.life.xg.utils.dbm.dao.XGDSRCDAO;
import com.csc.fsg.life.xg.utils.dbm.model.XGDSRCModel;

public class CrossDBDsrcFix {

	public static void main(String[] args) throws SQLException, Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("config/xmlg-config.xml");
		EnvironmentFactory.setApplicationConfig((ApplicationConfig) context.getBean("xmlgConfig"));
		EnvironmentInitializer.setServerDocRoot("D:/Projects/XGBase/lifewsWeb/webApplication");
		EnvironmentInitializer.init(null);

		String sourceEnv = "BASE";
		String targetEnv = "NBA59";

		System.out.println("Source Env: " + sourceEnv);
		System.out.println("Target Env: " + targetEnv);
		
		XGDSRCDAO baseDsrcDao = new XGDSRCDAO(sourceEnv);
		DAOModelCollection<XGDSRCModel> dsrcList = baseDsrcDao.getList();

		XGDSRCDAO nbaDsrcDao = new XGDSRCDAO(targetEnv);
		DAOModelCollection<XGDSRCModel> nbaDsrcList = nbaDsrcDao.getList();

		System.out.println("Total Dsrc count : " + nbaDsrcList.size());
		for (int i = 0; i < nbaDsrcList.size(); i++) {

			XGDSRCModel nbaModel = (XGDSRCModel) nbaDsrcList.get(i);
			for (int j = 0; j < dsrcList.size(); j++) {
				XGDSRCModel baseModel = (XGDSRCModel) dsrcList.get(j);
				if (nbaModel.getLabel().equalsIgnoreCase(baseModel.getLabel())) {
					nbaModel.setSchemaRoot(baseModel.getSchemaRoot());
					int k = nbaDsrcDao.update(nbaModel);
					if (k <= 0) {
						System.out.println(i + " *** Dsrc Errored on: " + nbaModel.getLabel());
					} else {
						System.out.println(i + " Dsrc Successful : " + nbaModel.getLabel());
					}
					break;
				}
			}
		}
		System.out.println("Update Successful.");
	}
}
