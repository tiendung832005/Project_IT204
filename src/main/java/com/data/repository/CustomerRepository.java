package com.data.repository;

import com.data.entity.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerRepository {
    @Autowired
    private SessionFactory sessionFactory;

    public List<Customer> getCustomers(int page, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            Query<Customer> query = session.createQuery("FROM Customer ORDER BY id ASC", Customer.class);
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
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Log thông tin customer trước khi lưu
            System.out.println("Saving customer: " + customer.toString());

            session.persist(customer);
            session.flush();

            transaction.commit();
            System.out.println("Customer saved successfully with ID: " + customer.getId());
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error saving customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public boolean existsByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Customer WHERE email = :email", Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByEmailAndIdNot(String email, Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Customer WHERE email = :email AND id != :id",
                    Long.class);
            query.setParameter("email", email);
            query.setParameter("id", id);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatus(Integer id, String newStatus) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Customer customer = session.get(Customer.class, id);
            if (customer != null) {
                customer.setStatus(newStatus);
                session.update(customer);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public Customer findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Customer.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateCustomer(Customer customer) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(customer);
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

    public boolean existsByPhone(String phone) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Customer WHERE phone = :phone", Long.class);
            query.setParameter("phone", phone);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existsByPhoneAndIdNot(String phone, Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Customer WHERE phone = :phone and id != :id",
                    Long.class);
            query.setParameter("phone", phone);
            query.setParameter("id", id);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
