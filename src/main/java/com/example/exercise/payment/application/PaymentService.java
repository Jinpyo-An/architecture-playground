package com.example.exercise.payment.application;

import java.time.LocalDateTime;
import java.util.List;

import com.example.exercise.payment.application.event.PaymentConfirmedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.exercise.payment.application.dto.PaymentCommand;
import com.example.exercise.payment.application.dto.PaymentConfirmation;
import com.example.exercise.payment.application.dto.PaymentFailCommand;
import com.example.exercise.payment.application.dto.PaymentFailureInfo;
import com.example.exercise.payment.application.dto.PaymentInfo;
import com.example.exercise.payment.domain.model.Payment;
import com.example.exercise.payment.domain.model.PaymentFailure;
import com.example.exercise.payment.domain.repository.PaymentFailureRepository;
import com.example.exercise.payment.domain.repository.PaymentRepository;
import com.example.exercise.payment.infrastructure.acl.TossPaymentAcl;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentFailureRepository paymentFailureRepository;
    private final TossPaymentAcl tossPaymentAcl;
    private final ApplicationEventPublisher eventPublisher;

    public ResponseEntity<List<PaymentInfo>> findAll(Pageable pageable) {
        Page<Payment> page = paymentRepository.findAll(pageable);
        List<PaymentInfo> payments = page.stream()
          .map(PaymentInfo::from)
          .toList();
        return ResponseEntity.status(HttpStatus.OK).body(payments);
    }

    @Transactional
    public ResponseEntity<PaymentInfo> confirm(PaymentCommand command) {
        PaymentConfirmation confirmation = tossPaymentAcl.confirm(command);
        //        UUID orderId = UUID.fromString(confirmation.orderId());
        //        PurchaseOrder order = orderService.findEntity(orderId);
        Payment payment = Payment.create(
          confirmation.paymentKey(),
          confirmation.orderId(),
          confirmation.totalAmount()
        );

        payment.markConfirmed(confirmation.method(), confirmation.approvedAt(), confirmation.requestedAt());

        Payment saved = paymentRepository.save(payment);
        eventPublisher.publishEvent(new PaymentConfirmedEvent(
                saved.getId(),
                saved.getOrderId(),
                saved.getPaymentKey(),
                saved.getAmount(),
                saved.getApprovedAt()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentInfo.from(saved));
    }

    public ResponseEntity<PaymentFailureInfo> recordFailure(PaymentFailCommand command) {
        PaymentFailure failure = PaymentFailure.from(
          command.orderId(),
          command.paymentKey(),
          command.errorCode(),
          command.errorMessage(),
          command.amount(),
          command.rawPayload()
        );
        PaymentFailure saved = paymentFailureRepository.save(failure);
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentFailureInfo.from(saved));
    }
}
