package nz.co.ksktech.kiwisaver.fee.repository;

import nz.co.ksktech.kiwisaver.fee.model.Fund;

import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FundRepository {

    private final List<Fund> funds;

    public FundRepository() {
        this.funds = List.of(
                new Fund("conservative-1", "KiwiSaver Conservative Fund", "Conservative", new BigDecimal("0.45")),
                new Fund("balanced-1", "KiwiSaver Balanced Fund", "Balanced", new BigDecimal("0.60")),
                new Fund("growth-1", "KiwiSaver Growth Fund", "Growth", new BigDecimal("0.80")),
                new Fund("ethical-growth-1", "Ethical Growth Fund", "Growth", new BigDecimal("0.95")),
                new Fund("retirement-1", "KiwiSaver Retirement Fund", "Conservative", new BigDecimal("0.35"))
        );
    }

    public List<Fund> listAll() {
        return funds;
    }

    public Optional<Fund> findById(String id) {
        return funds.stream().filter(f -> f.id().equals(id)).findFirst();
    }
}
