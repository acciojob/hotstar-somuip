package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setUser(user);
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        // check how much amount he/she has to pay for subscription

        int totalCostBasic= 500 + (200 * subscriptionEntryDto.getNoOfScreensRequired());
        int totalCostPro = 800 + (250 * subscriptionEntryDto.getNoOfScreensRequired());
        int totalCostElite = 1000 + (350 * subscriptionEntryDto.getNoOfScreensRequired());

        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setTotalAmountPaid(totalCostBasic);
        } else if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)) {
            subscription.setTotalAmountPaid(totalCostPro);
        }else {
            subscription.setTotalAmountPaid(totalCostElite);
        }

        subscriptionRepository.save(subscription);
        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user;
        try{
            user = userRepository.findById(userId).get();
        }catch (Exception e){
            return -1;
        }

        Subscription subscription = user.getSubscription();
        int prevPaid = subscription.getTotalAmountPaid();
        int currPaid = 0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }else if (subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            currPaid = 1000 + (350 * user.getSubscription().getNoOfScreensSubscribed());
            subscription.setSubscriptionType(SubscriptionType.ELITE);

        }else {
            currPaid = 800 + (250 * user.getSubscription().getNoOfScreensSubscribed());
            subscription.setSubscriptionType(SubscriptionType.PRO);
        }

        subscriptionRepository.save(subscription);


        return currPaid - prevPaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        int revenue = 0;

        for(Subscription subscription : subscriptionList){
            revenue += subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
