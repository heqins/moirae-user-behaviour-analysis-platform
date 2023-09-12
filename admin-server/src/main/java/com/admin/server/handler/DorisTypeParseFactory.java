package com.admin.server.handler;

import com.api.common.enums.AttributeDataTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DorisTypeParseFactory {

    private final Logger logger = LoggerFactory.getLogger(DorisTypeParseFactory.class);

    public DorisTypeParser getDorisTypeParser(Class c) {
        try {
            return (DorisTypeParser) Class.forName(c.getName()).newInstance();
        }catch (Exception e) {
            logger.error("");
        }

        return null;
    }
}
