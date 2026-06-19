class DiscountStrategy {
    calculate(totalAmount) {
        return 0;
    }
}

class RegularDiscount extends DiscountStrategy {
    calculate(totalAmount) {
        return totalAmount * 0.05;
    }
}

class PremiumDiscount extends DiscountStrategy {
    calculate(totalAmount) {
        return totalAmount * 0.15;
    }
}

class VIPDiscount extends DiscountStrategy {
    calculate(totalAmount) {
        return totalAmount * 0.30;
    }
}

class OrderCalculator {
    constructor(strategies) {
        this.strategies = {
            'REGULAR': new RegularDiscount(),
            'PREMIUM': new PremiumDiscount(),
            'VIP': new VIPDiscount()
        };
    }

    calculateDiscount(userType, totalAmount) {
        const strategy = this.strategies[userType] || new DiscountStrategy();
        return strategy.calculate(totalAmount);
    }
}
