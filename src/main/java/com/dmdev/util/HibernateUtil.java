package com.dmdev.util;

import com.dmdev.converter.BirthdayConverter;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@UtilityClass
public class HibernateUtil {


    public static SessionFactory buildSessionFactory(){
        var configuration = buildConfiguration();
        configuration.configure();
        return configuration.buildSessionFactory();
    }

    public static Configuration buildConfiguration() {
        Configuration configuration = new Configuration();
        configuration.addAttributeConverter(new BirthdayConverter());
        return configuration;
    }
}
