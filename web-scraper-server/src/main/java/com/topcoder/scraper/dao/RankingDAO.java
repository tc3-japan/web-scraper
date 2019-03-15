package com.topcoder.scraper.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "product_category_ranking")
public class RankingDAO {

  @Embeddable
  public static class ProductCategoryId implements Serializable {

    @Column(name = "product_id")
    private int productId;

    @Column(name = "category_id")
    private int categoryId;

    public ProductCategoryId() {
    }

    public ProductCategoryId(int productId, int categoryId) {
      this.productId = productId;
      this.categoryId = categoryId;
    }

    public int getProductId() {
      return productId;
    }

    public int getCategoryId() {
      return categoryId;
    }

    public void setProductId(int productId) {
      this.productId = productId;
    }

    public void setCategoryId(int categoryId) {
      this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;

      if (o == null || getClass() != o.getClass()) return false;

      ProductCategoryId that = (ProductCategoryId) o;

      return new EqualsBuilder()
        .append(productId, that.productId)
        .append(categoryId, that.categoryId)
        .isEquals();
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder(17, 37)
        .append(productId)
        .append(categoryId)
        .toHashCode();
    }
  }

  @EmbeddedId
  private ProductCategoryId id;

  /**
   * Product dao
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("productId")
  private ProductDAO product;

  /**
   * Category dao
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("categoryId")
  private CategoryDAO category;

  /**
   * Ranking
   */
  @Column(name = "ranking")
  private int ranking;

  /**
   * Update at
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_at")
  private Date updateAt;

  public RankingDAO() {
  }

  public RankingDAO(ProductDAO product, CategoryDAO category) {
    this.product = product;
    this.category = category;
  }

  public RankingDAO(ProductDAO product, CategoryDAO category, int ranking, Date updateAt) {
    this.id = new ProductCategoryId(product.getId(), category.getId());
    this.product = product;
    this.category = category;
    this.ranking = ranking;
    this.updateAt = updateAt;
  }

  public ProductCategoryId getId() {
    return id;
  }

  public ProductDAO getProduct() {
    return product;
  }

  public CategoryDAO getCategory() {
    return category;
  }

  public int getRanking() {
    return ranking;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setId(ProductCategoryId id) {
    this.id = id;
  }

  public void setProduct(ProductDAO product) {
    this.product = product;
  }

  public void setCategory(CategoryDAO category) {
    this.category = category;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }
}
