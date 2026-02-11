package c8102ea2a569.service2tomcat.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "grammy_rewards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"band_id", "genre"}))
public class GrammyRewardEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "band_id", nullable = false)
    private Integer bandId;

    @NotNull
    @Column(nullable = false, length = 50)
    private String genre;

    @NotNull
    @Column(name = "reward_date", nullable = false)
    private LocalDateTime rewardDate;

    public GrammyRewardEntity() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getBandId() { return bandId; }
    public void setBandId(Integer bandId) { this.bandId = bandId; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public LocalDateTime getRewardDate() { return rewardDate; }
    public void setRewardDate(LocalDateTime rewardDate) { this.rewardDate = rewardDate; }
}
