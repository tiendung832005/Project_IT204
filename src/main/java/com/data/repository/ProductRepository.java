package com.data.repository;

import com.data.dto.ProductDTO;
import com.data.entity.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {
    @Autowired
    private SessionFactory sessionFactory;

    public List<Product> findAll(int page, int size) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Query<Product> query = session.createQuery("FROM Product", Product.class);
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            List<Product> products = query.list();

            System.out.println("==> Query returned " + (products == null ? "null" : products.size()) + " products");

            transaction.commit();
            return products;
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

    public long countTotalProducts() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Product", Long.class);
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

    public List<Product> searchProducts(String search, BigDecimal minPrice, BigDecimal maxPrice, Integer stock, int page, int pageSize) {
        Session session = null;
        Transaction tx = null;
        List<Product> products = new ArrayList<>();

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            StringBuilder hql = new StringBuilder("FROM Product WHERE 1=1");

            if (search != null && !search.trim().isEmpty()) {
                hql.append(" AND LOWER(brand) LIKE :search");
            }
            if (minPrice != null) {
                hql.append(" AND price >= :minPrice");
            }
            if (maxPrice != null) {
                hql.append(" AND price <= :maxPrice");
            }
            if (stock != null) {
                hql.append(" AND stock >= :stock");
            }

            Query<Product> query = session.createQuery(hql.toString(), Product.class);

            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.toLowerCase() + "%");
            }
            if (minPrice != null) {
                query.setParameter("minPrice", minPrice);
            }
            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }
            if (stock != null) {
                query.setParameter("stock", stock);
            }

            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);

            products = query.getResultList();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return products;
    }

    public long countProducts(String search, BigDecimal minPrice, BigDecimal maxPrice, Integer stock) {
        Session session = null;
        Transaction tx = null;
        long count = 0;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            StringBuilder hql = new StringBuilder("SELECT COUNT(*) FROM Product WHERE 1=1");

            if (search != null && !search.trim().isEmpty()) {
                hql.append(" AND LOWER(brand) LIKE :search");
            }
            if (minPrice != null) {
                hql.append(" AND price >= :minPrice");
            }
            if (maxPrice != null) {
                hql.append(" AND price <= :maxPrice");
            }
            if (stock != null) {
                hql.append(" AND stock >= :stock");
            }

            Query<Long> query = session.createQuery(hql.toString(), Long.class);

            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.toLowerCase() + "%");
            }
            if (minPrice != null) {
                query.setParameter("minPrice", minPrice);
            }
            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }
            if (stock != null) {
                query.setParameter("stock", stock);
            }

            count = query.uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return count;
    }


    public boolean save(Product product) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.save(product);

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

    public boolean deleteById(Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Product product = session.get(Product.class, id);
            if (product != null) {
                session.delete(product);
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

    public Product findById(Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Product product = session.get(Product.class, id);

            transaction.commit();
            return product;
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

    public boolean update(Product product) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            session.update(product);

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

    public boolean existsByNameAndIdNot(String name, Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Product WHERE name = :name AND id != :id",
                    Long.class);
            query.setParameter("name", name);
            query.setParameter("id", id);

            Long count = query.uniqueResult();

            transaction.commit();
            return count != null && count > 0;
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

    public boolean existsByName(String name) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Query<Long> query = session.createQuery(
                    "SELECT COUNT(*) FROM Product WHERE name = :name",
                    Long.class);
            query.setParameter("name", name);

            Long count = query.uniqueResult();

            transaction.commit();
            return count != null && count > 0;
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