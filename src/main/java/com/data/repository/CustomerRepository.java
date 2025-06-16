package com.data.repository;

import com.data.entity.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerRepository {
    @Autowired
    private SessionFactory sessionFactory;

    public List<Customer> getCustomers(int page, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            Query<Customer> query = session.createQuery("FROM Customer ORDER BY id DESC", Customer.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countTotalCustomers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Customer", Long.class);
            return query.uniqueResult();
        }
    }

    public boolean save(Customer customer) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(customer);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Customer WHERE email = :email", Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        }
    }

    public boolean existsByEmailAndIdNot(String email, Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Customer WHERE email = :email AND id != :id",
                    Long.class);
            query.setParameter("email", email);
            query.setParameter("id", id);
            return query.uniqueResult() > 0;
        }
    }
}
