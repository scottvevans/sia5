package tacos.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Taco implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  private Date createdAt;

  @Size(min=5, message="Name must be at least 5 characters long")
  private String name;

  @ManyToMany(targetEntity=Ingredient.class)
  @NotNull(message = "You must choose at least 1 ingredient")
  @Enumerated(EnumType.STRING)
  private List<Ingredient> ingredients;
  
  @PrePersist
  void prePersist() {
    this.createdAt = new Date();
  }

}