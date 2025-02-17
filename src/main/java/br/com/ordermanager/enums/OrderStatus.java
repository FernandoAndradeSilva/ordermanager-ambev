package br.com.ordermanager.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    // Método para facilitar a busca de um status pelo nome
    public static OrderStatus fromDescription(String description) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        return null;
    }

}