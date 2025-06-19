package com.data.repository;

import com.data.entity.InvoiceDetail;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvoiceDetailRepository {
    @Autowired
    private SessionFactory sessionFactory;

    public boolean saveAll(List<InvoiceDetail> details) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            for (InvoiceDetail detail : details) {
                session.save(detail);
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<InvoiceDetail> findByInvoiceId(Integer invoiceId) {
        try (Session session = sessionFactory.openSession()) {
            Query<InvoiceDetail> query = session.createQuery(
                    "FROM InvoiceDetail d LEFT JOIN FETCH d.product WHERE d.invoice.id = :invoiceId",
                    InvoiceDetail.class);
            query.setParameter("invoiceId", invoiceId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
