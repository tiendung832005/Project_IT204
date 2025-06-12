package com.data.repository;

import com.data.entity.Admin;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AdminRepository {
    @Autowired
    private SessionFactory sessionFactory;

    public Admin findByUsername(String username) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Query<Admin> query = session.createQuery("FROM Admin WHERE username = :username", Admin.class);
            query.setParameter("username", username);
            Admin admin = query.uniqueResult();

            transaction.commit();
            System.out.println("Found admin: " + (admin != null ? admin.getUsername() : "null"));
            return admin;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Error finding admin: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
