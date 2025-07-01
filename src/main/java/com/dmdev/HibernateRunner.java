package com.dmdev;

import com.dmdev.converter.BirthdayConverter;
import com.dmdev.entity.*;
import com.dmdev.util.HibernateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;


@Slf4j
public class HibernateRunner {

//    private static final Logger log = LoggerFactory.getLogger(HibernateRunner.class);


    @SneakyThrows
    public static void main(String[] args) {

        var company = Company.builder()
                .name("Amazon")
                .build();

        var user = User.builder()
                .username("nikitamorozka70@gmail.com")
                .personalInfo(PersonalInfo.builder()
                        .firstname("Niiiikita")
                        .lastname("Moorozov")
                        .birthDate(LocalDate.now())
                        .build())
                .company(company)
                .role(Role.USER)
                .build();

//        log.info("User entity is in transient state, object: {} ", user);


        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
            var session1 = sessionFactory.openSession();
            try (session1) {
                var transaction = session1.beginTransaction();
//                log.trace("Transaction is created, {}", transaction);

//                session1.persist(company);
//                session1.persist(user);
//                var user1 = session1.get(User.class, 1L);
//                session1.evict(user1);
                session1.persist(user);

                session1.getTransaction().commit();
            }
//            log.warn("User is in detached state: {}, session is closed {}", user, session1);
        }

    }

}
