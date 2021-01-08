package de.eldoria.hotsprings.config;

import lombok.Data;

import java.util.UUID;

@Data
public class Limit {
    private UUID uuid;
    private int expLimit = 0;
    private int moneyLimit = 0;
    private int intervalLimit = 0;

    public Limit(UUID uniqueId) {
        uuid = uniqueId;
    }

    public Limit(int expLimit, int moneyLimit) {
        uuid = null;
        this.expLimit = expLimit;
        this.moneyLimit = moneyLimit;
        intervalLimit = 1;
    }

    public void apply(Limit limit) {
        expLimit += limit.expLimit;
        moneyLimit += moneyLimit;
        intervalLimit += intervalLimit;
    }

    public boolean canReceive(LimitType limitType, SpringSettings settings) {
        switch (limitType) {
            case EXPERIENCE:
                return expLimit <= settings.getMaxExperience();
            case MONEY:
                return moneyLimit <= settings.getMaxMoney();
            case INTERVAL:
                return intervalLimit <= settings.getMaxIntervals();
        }
        return true;
    }
}
