package com.doritech.api.Common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.doritech.api.Repository.TicketDetailsRepository;
@Component
public class CommonUtils {
    
    private static Map<String, Integer> referenceMap = new HashMap<>();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    
    

    public static String genernateTicketNumber() {
        String currentDate = dateFormat.format(new Date());

        if (!referenceMap.containsKey(currentDate)) {
            referenceMap.put(currentDate, 1);
        } else {
            referenceMap.put(currentDate, referenceMap.get(currentDate) + 1);
        }

        int orderNumber = referenceMap.get(currentDate);
        String formattedOrderNumber = String.format("%06d", orderNumber);
        String ticketNumber = currentDate + formattedOrderNumber;


        return ticketNumber;
    }
}
