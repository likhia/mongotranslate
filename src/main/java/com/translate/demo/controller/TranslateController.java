package com.translate.demo.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.translate.demo.data.InputData;

@RestController
public class TranslateController {
    
    @Autowired
    private ResourceLoader resourceLoader;


    @PostMapping("/translate")
    public String translate(@RequestBody InputData inputData) {

        System.out.println(inputData.getQuery());

        System.out.println(inputData.getDbName());

        return convertSQLtoMQL(inputData);
    }

    private String convertSQLtoMQL(InputData inputdata) {
        StringBuffer mql = new StringBuffer(); 

        try {
            Resource resource = resourceLoader.getResource("classpath:schema.drdl");
            String filePath = resource.getFile().getAbsolutePath();
	
            //String filePath = "/Users/jasmine.lim/dbs/mot/demo/src/main/resources/schema.drdl";
    
            //changes made on 1 Feb 2024.  Special handling for text field with value contain space and used for query.
            String command = "mongotranslate --query  --dbName " + inputdata.getDbName() + " --schema " + filePath + "";
            
            System.out.println(command);
            
            StringTokenizer st = new StringTokenizer(command);
            int totalSize = st.countTokens()+1;
            String[] cmdarray = new String[totalSize];
            for (int i = 0; st.hasMoreTokens(); i++) {
                //changes made on 1 Feb 2024.  Special handling for text field with value contain space and used for query.
                String token = st.nextToken();
                cmdarray[i] = token;

                if (token.equals("--query")) {
                    i++;
                    cmdarray[i] = inputdata.getQuery();        
                } 

                //System.out.println(cmdarray[i]);
            }
            
            Process process = new ProcessBuilder(cmdarray).start();
            
            int waitFor = process.waitFor();
            System.out.println("waitFor:: " + waitFor);
            BufferedReader success = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String s = "";
            
            while ((s = success.readLine()) != null) {
                 mql.append(s);
            }

            
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream())); //not sure why the output of this execution is in standard error. 

            s = "";
            
            while ((s = error.readLine()) != null) {
                System.out.println(s);
            }

            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mql.toString();
    }
}
