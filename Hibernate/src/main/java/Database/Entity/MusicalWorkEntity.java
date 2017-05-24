package Database.Entity;

import javax.persistence.*;

/**
 * @author Benjamin Grabherr
 */
@Entity
@Table(name = "MusicalWork", schema = "sem4_team2")
public class MusicalWorkEntity {
    private int musicalWorkId;
    private InstrumentationEntity instrumentationId;
    private String name;
    private String composer;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "musicalWorkID", nullable = false)
    public int getMusicalWorkId() {
        return musicalWorkId;
    }

    public void setMusicalWorkId(int musicalWorkId) {
        this.musicalWorkId = musicalWorkId;
    }

    @ManyToOne
    @JoinColumn(name = "instrumentationID", nullable = false, referencedColumnName = "instrumentationID")
    public InstrumentationEntity getInstrumentationId() {
        return instrumentationId;
    }

    public void setInstrumentationId(InstrumentationEntity instrumentationId) {
        this.instrumentationId = instrumentationId;
    }

    @Basic
    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "composer")
    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MusicalWorkEntity that = (MusicalWorkEntity) o;

        if (musicalWorkId != that.musicalWorkId) return false;
        if (instrumentationId != that.instrumentationId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (composer != null ? !composer.equals(that.composer) : that.composer != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = musicalWorkId;
        result = 31 * result + instrumentationId.getInstrumentationId();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (composer != null ? composer.hashCode() : 0);
        return result;
    }
}
