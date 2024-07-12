package myBankApplication.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "currency_exchange_rates")
class CurrencyExchangeRate {

    @JsonProperty("provider")
    @Column(name = "provider")
    private String provider;

    @JsonProperty("WARNING_UPGRADE_TO_V6")
    @Column(name = "warning_upgrade_to_v6")
    private String warningUpgradeToV6;

    @JsonProperty("terms")
    @Column(name = "terms")
    private String terms;

    @JsonProperty("base")
    @Column(name = "base")
    private String base;

    @JsonProperty("date")
    @Column(name = "date")
    private LocalDate date;

    @JsonProperty("time_last_updated")
    @Column(name = "time_last_updated")
    private Integer timeLastUpdated;

//    @JsonProperty("rates")
//    @ElementCollection
//    @CollectionTable(name = "exchange_rate", joinColumns = @JoinColumn(name = "currency_exchange_rate_id"))
//    @MapKeyColumn(name = "currency")
//    @Column(name = "rate")
//    private Map<String, Double> rates = new HashMap<>();


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Getter
    @Setter
    @Id
    private Long id;



    


}
