import javax.persistence.*;

@Entity
@Table(name = "docs")
public class Doc {

    @Id
    @SequenceGenerator(name = "docs", sequenceName = "docs_gen", initialValue = 10, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "docs")
    private Long id;

    private String number;

    private String description;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void copyProperties(Doc newdoc) {
        this.number = newdoc.number;
        this.description = newdoc.description;
    }
}
