package Database.Entity;

import Enums.SectionType;

import javax.persistence.*;

/**
 * @author Benjamin Grabherr
 */
@Entity
@Table(name = "Part", schema = "sem4_team2")
public class PartEntity {
    private int partId;
    private PartTypeEntity partType;
    private SectionType sectionType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partID", nullable = false)
    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    @OneToOne
    @JoinColumn(name = "partType", nullable = false, referencedColumnName = "partTypeID")
    public PartTypeEntity getPartType() {
        return partType;
    }

    public void setPartType(PartTypeEntity partType) {
        this.partType = partType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "sectionType", nullable = false, columnDefinition = "eum('Violin1','Violin2','Viola','Violincello','Doublebass','Woodwind','Brass','Percussion')")
    public SectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(SectionType sectionType) {
        this.sectionType = sectionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartEntity that = (PartEntity) o;

        if (partId != that.partId) return false;
        if (partType != that.partType) return false;
        if (sectionType != null ? !sectionType.equals(that.sectionType) : that.sectionType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = partId;
        result = 31 * result + partType.getPartTypeId();
        result = 31 * result + (sectionType != null ? sectionType.hashCode() : 0);
        return result;
    }
}
