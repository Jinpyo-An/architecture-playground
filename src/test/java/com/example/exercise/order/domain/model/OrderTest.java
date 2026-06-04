package com.example.exercise.order.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static Order newOrder(String status) {
        Order order = Order.create(
                null,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new BigDecimal("10000"),
                new BigDecimal("1000"),
                BigDecimal.ZERO,
                status,
                null,
                UUID.randomUUID()
        );
        order.onCreate();
        return order;
    }

    @Test
    @DisplayName("status를 지정하지 않으면 디폴트는 PAYMENT_WAIT")
    void defaultStatusIsPaymentWait() {
        Order order = newOrder(null);

        assertThat(order.getStatus()).isEqualTo("PAYMENT_WAIT");
    }

    @Test
    @DisplayName("markCanceled: PAYMENT_WAIT 상태인 주문은 CANCELED로 전이된다")
    void markCanceledOnPaymentWait() {
        Order order = newOrder(null);
        UUID actor = UUID.randomUUID();

        order.markCanceled("AUTO_TIMEOUT", actor);

        assertThat(order.getStatus()).isEqualTo("CANCELED");
        assertThat(order.getCancelReason()).isEqualTo("AUTO_TIMEOUT");
        assertThat(order.getCanceledAt()).isNotNull();
        assertThat(order.getModifyId()).isEqualTo(actor);
    }

    @Test
    @DisplayName("markCanceled: PAID 상태 주문은 취소할 수 없다")
    void markCanceledOnPaidThrows() {
        Order order = newOrder(null);
        order.markPaid(null, UUID.randomUUID());

        assertThatThrownBy(() -> order.markCanceled("AUTO_TIMEOUT", UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only PAYMENT_WAIT orders can be canceled");
    }

    @Test
    @DisplayName("markCanceled: 이미 CANCELED 상태인 주문은 다시 취소할 수 없다 (멱등성/가드)")
    void markCanceledOnAlreadyCanceledThrows() {
        Order order = newOrder(null);
        order.markCanceled("AUTO_TIMEOUT", UUID.randomUUID());

        assertThatThrownBy(() -> order.markCanceled("AUTO_TIMEOUT", UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("markPaid: CANCELED 상태인 주문은 PAID로 덮어쓸 수 없다 (TOCTOU 가드)")
    void markPaidOnCanceledThrows() {
        Order order = newOrder(null);
        order.markCanceled("AUTO_TIMEOUT", UUID.randomUUID());

        assertThatThrownBy(() -> order.markPaid(null, UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot mark canceled order as paid");
    }

    @Test
    @DisplayName("markPaid: PAYMENT_WAIT 상태인 주문은 PAID로 전이된다")
    void markPaidOnPaymentWait() {
        Order order = newOrder(null);

        order.markPaid(null, UUID.randomUUID());

        assertThat(order.getStatus()).isEqualTo("PAID");
        assertThat(order.getPaidAt()).isNotNull();
    }
}
