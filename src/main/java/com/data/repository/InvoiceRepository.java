package com.data.repository;

import com.data.entity.Invoice;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class InvoiceRepository {
    @Autowired
    private SessionFactory sessionFactory;

    public boolean save(Invoice invoice) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(invoice);
            session.flush();
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

    public List<Invoice> findAll() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query<Invoice> query = session.createQuery("FROM Invoice ORDER BY createdAt DESC", Invoice.class);
            List<Invoice> invoices = query.list();
            transaction.commit();
            return invoices;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Invoice> searchInvoices(String search, String dateFrom, String dateTo) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            StringBuilder hql = new StringBuilder();
            hql.append("FROM Invoice i LEFT JOIN FETCH i.customer WHERE 1=1");

            if (search != null && !search.trim().isEmpty()) {
                hql.append(" AND LOWER(i.customer.name) LIKE LOWER(:search)");
            }

            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                hql.append(" AND CAST(i.createdAt AS date) >= :dateFrom");
            }

            if (dateTo != null && !dateTo.trim().isEmpty()) {
                hql.append(" AND CAST(i.createdAt AS date) <= :dateTo");
            }

            hql.append(" ORDER BY i.createdAt DESC");

            Query<Invoice> query = session.createQuery(hql.toString(), Invoice.class);

            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }

            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fromDate = sdf.parse(dateFrom);
                    query.setParameter("dateFrom", fromDate);
                } catch (Exception e) {
                    System.out.println("Error parsing dateFrom: " + dateFrom);
                }
            }

            if (dateTo != null && !dateTo.trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date toDate = sdf.parse(dateTo);
                    query.setParameter("dateTo", toDate);
                } catch (Exception e) {
                    System.out.println("Error parsing dateTo: " + dateTo);
                }
            }

            List<Invoice> invoices = query.list();
            transaction.commit();
            return invoices;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public long countInvoices(String search, String dateFrom, String dateTo) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            StringBuilder hql = new StringBuilder();
            hql.append("SELECT COUNT(i) FROM Invoice i WHERE 1=1");

            if (search != null && !search.trim().isEmpty()) {
                hql.append(" AND LOWER(i.customer.name) LIKE LOWER(:search)");
            }

            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                hql.append(" AND CAST(i.createdAt AS date) >= :dateFrom");
            }

            if (dateTo != null && !dateTo.trim().isEmpty()) {
                hql.append(" AND CAST(i.createdAt AS date) <= :dateTo");
            }

            Query<Long> query = session.createQuery(hql.toString(), Long.class);

            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }

            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fromDate = sdf.parse(dateFrom);
                    query.setParameter("dateFrom", fromDate);
                } catch (Exception e) {
                    System.out.println("Error parsing dateFrom: " + dateFrom);
                }
            }

            if (dateTo != null && !dateTo.trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date toDate = sdf.parse(dateTo);
                    query.setParameter("dateTo", toDate);
                } catch (Exception e) {
                    System.out.println("Error parsing dateTo: " + dateTo);
                }
            }

            Long count = query.uniqueResult();
            transaction.commit();
            return count != null ? count : 0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return 0;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Invoice> searchInvoicesWithPagination(String search, String dateFrom, String dateTo, int page,
                                                      int pageSize) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            StringBuilder hql = new StringBuilder();
            hql.append("FROM Invoice i LEFT JOIN FETCH i.customer WHERE 1=1");

            if (search != null && !search.trim().isEmpty()) {
                hql.append(" AND LOWER(i.customer.name) LIKE LOWER(:search)");
            }

            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                hql.append(" AND CAST(i.createdAt AS date) >= :dateFrom");
            }

            if (dateTo != null && !dateTo.trim().isEmpty()) {
                hql.append(" AND CAST(i.createdAt AS date) <= :dateTo");
            }

            hql.append(" ORDER BY i.createdAt DESC");

            Query<Invoice> query = session.createQuery(hql.toString(), Invoice.class);

            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.trim() + "%");
            }

            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fromDate = sdf.parse(dateFrom);
                    query.setParameter("dateFrom", fromDate);
                } catch (Exception e) {
                    System.out.println("Error parsing dateFrom: " + dateFrom);
                }
            }

            if (dateTo != null && !dateTo.trim().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date toDate = sdf.parse(dateTo);
                    query.setParameter("dateTo", toDate);
                } catch (Exception e) {
                    System.out.println("Error parsing dateTo: " + dateTo);
                }
            }

            // Set pagination
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);

            List<Invoice> invoices = query.list();
            transaction.commit();
            return invoices;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Invoice findById(Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Sử dụng HQL để load customer cùng với invoice
            Query<Invoice> query = session.createQuery(
                    "FROM Invoice i LEFT JOIN FETCH i.customer WHERE i.id = :id",
                    Invoice.class);
            query.setParameter("id", id);
            Invoice invoice = query.uniqueResult();

            transaction.commit();
            return invoice;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public boolean updateStatus(Integer id, Invoice.InvoiceStatus newStatus) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Invoice invoice = session.get(Invoice.class, id);
            if (invoice != null) {
                invoice.setStatus(newStatus);
                session.update(invoice);
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
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
