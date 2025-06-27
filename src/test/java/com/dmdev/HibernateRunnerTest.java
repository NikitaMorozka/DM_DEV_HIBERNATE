package com.dmdev;

import com.dmdev.entity.*;
import com.dmdev.util.HibernateTestUtil;
import com.dmdev.util.HibernateUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Cleanup;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.*;
import static java.util.stream.Collectors.joining;


class HibernateRunnerTest {

    @Test
    void check2DataBase(){
        try (var sessionFactory = HibernateTestUtil.buildConfiguration();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var company = Company.builder().name("Google").build();


            session.getTransaction().commit();

        }
    }


    @Test
    void localeInfo(){
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            var company = session.get(Company.class, 1);
//            company.getLocales().add(LocaleInfo.of("ru", "описание на русском"));
//            company.getLocales().add(LocaleInfo.of("en", "English to description"));

            company.getUsers().forEach((k, v) -> System.out.println(v));



            session.getTransaction().commit();

        }
    }


    @Test
    void checkManyToMany() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {

            session.beginTransaction();

            var user = session.get(User.class, 9L);
            var chat = session.get(Chat.class, 1L);

            var userChat = UsersChat.builder()
                    .crestedAt(Instant.now())
                    .createdBy(user.getUsername())
                    .build();

            userChat.setUser(user);
            userChat.setChat(chat);

            session.persist(userChat);

//            var user = User.builder()
//                    .username("nikitamorozka130@gmail.com")
//                    .build();
//
//            var profile = Profile.builder()
//                    .language("ru")
//                    .street("Киевский 18а")
//                    .build();
//            profile.setUser(user);
//
//            session.persist(user);

            session.getTransaction().commit();

        }
    }

    @Test
    void checkOneToOne() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            var user = session.get(User.class, 9L);
//            var user = User.builder()
//                    .username("nikitamorozka130@gmail.com")
//                    .build();
//
//            var profile = Profile.builder()
//                    .language("ru")
//                    .street("Киевский 18а")
//                    .build();
//            profile.setUser(user);
//
//            session.persist(user);

            session.getTransaction().commit();

        }
    }


    @Test
    void checkOrhanRemoval() {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            Company company = session.getReference(Company.class, 1);

//            company.getUsers().removeIf(user -> user.getId().equals(1L));

            session.getTransaction().commit();


        }

    }

    @Test
    void checkLazyInitialisation() {
        Company company = null;
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();
            company = session.getReference(Company.class, 1);
            var users = company.getUsers();
            System.out.println(users.size());
            session.getTransaction().commit();


        }

    }

    @Test
    void deleteCompany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();
        var company = session.get(Company.class, 5);

        session.remove(company);
        session.getTransaction().commit();

    }

    @Test
    void addUserToNewCompany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = Company.builder()
                .name("Facebook")
                .build();

        var user = User.builder()
                .username("Света")
                .build();

        company.addUser(user);

        session.persist(company);


        session.getTransaction().commit();
    }


    @Test
    void oneToMany() {
        @Cleanup var sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup var session = sessionFactory.openSession();
        session.beginTransaction();

        var company = session.get(Company.class, 4);
        Hibernate.initialize(company);
        System.out.println(company.getUsers());

        session.getTransaction().commit();
    }

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PreparedStatement preparedStatement = null;
        var resultSet = preparedStatement.executeQuery();
        resultSet.getString("username");
        resultSet.getString("firstname");
        resultSet.getString("lastname");

        var userClass = User.class;

        var constructor = userClass.getConstructor();
        var user = constructor.newInstance();
        var usernameField = userClass.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(user, resultSet.getString("username"));
    }


    @Test
    void checkReflectionApi() throws SQLException {
        var user = User.builder()
                .build();


        String sql = """
                 insert
                    into
                        %s
                        (%s)
                    values
                        (%s)
                """;
        var tableName = ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getName());

        var declaredFields = user.getClass().getDeclaredFields();

        var columnNames = Arrays.stream(declaredFields)
                .map(field -> ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
//                        .filter(name -> !name.isBlank())
                        .orElse(field.getName()))
                .collect(joining(", "));

        var columnValues = Arrays.stream(declaredFields).map(field -> "?")
                .collect(joining(", "));


        System.out.println(sql.formatted(tableName, columnNames, columnValues));

        Connection connection = null;

        var prepareStatement = connection.prepareStatement(sql.formatted(tableName, columnNames, columnValues));


    }
}