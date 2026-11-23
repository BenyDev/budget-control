package com.benedykt.budget_control.payment.services;

import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.auth_users.services.UserService;
import com.benedykt.budget_control.category.entity.Category;
import com.benedykt.budget_control.category.repo.CategoryRepo;
import com.benedykt.budget_control.enums.ECategory;
import com.benedykt.budget_control.enums.EPaymentMethod;
import com.benedykt.budget_control.exceptions.NotFoudException;
import com.benedykt.budget_control.openAiApi.dtos.TransactionCategorizedByOpenAiResponse;
import com.benedykt.budget_control.payment.dtos.PaymentTransactionCategorizedResponse;
import com.benedykt.budget_control.payment.entity.Payment;
import com.benedykt.budget_control.payment.repo.PaymentRepo;
import com.benedykt.budget_control.payment_item.entity.PaymentItem;
import com.benedykt.budget_control.payment_item.repo.PaymentItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final UserService userService;
    private final CategoryRepo categoryRepo;
    private final PaymentItemRepo paymentItemRepo;

    @Override
    @Transactional
    public List<PaymentTransactionCategorizedResponse> addCategorizedTransactionsByOpenAI(List<TransactionCategorizedByOpenAiResponse> categorizedTransaction) {

        AppUser user = userService.getCurrentLoggedInUser();
        EPaymentMethod paymentByCard = EPaymentMethod.CARD;

        List<Payment> payments = new ArrayList<>(categorizedTransaction.size());
        List<PaymentItem> paymentItems = new ArrayList<>(categorizedTransaction.size());

        Category otherCategory = categoryRepo.findByName(ECategory.OTHER.name())
                        .orElseThrow(()-> new NotFoudException("ECategory OTHER not found"));

        categorizedTransaction.forEach(p -> {
            Payment payment = Payment.builder()
                    .name(p.getTitle())
                    .paymentDate(p.getTransactionDate())
                    .payer(user)
                    .paymentMethod(paymentByCard)
                    .build();

            payments.add(payment);


            Category itemCategory = categoryRepo.findByName(p.getECategory().name())
                    .orElse(otherCategory);

            PaymentItem paymentItem = PaymentItem.builder()
                    .name(p.getDescription())
                    .category(itemCategory)
                    .subcategory(null)
                    .amount(p.getAmount())
                    .payment(payment)
                    .build();

            paymentItems.add(paymentItem);
        });

        paymentRepo.saveAll(payments);
        paymentItemRepo.saveAll(paymentItems);


        return List.of();
    }

    @Override
    public List<PaymentTransactionCategorizedResponse> showCategorizedTransactionsByOpenAI(List<TransactionCategorizedByOpenAiResponse> categorizedTransaction) {
        return List.of();
    }
}
