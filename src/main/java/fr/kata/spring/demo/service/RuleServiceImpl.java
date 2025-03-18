package fr.kata.spring.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class RuleServiceImpl implements RuleService {
  
    @Override
    public String transform(Integer number) {
        StringBuilder sb = new StringBuilder("");
        if(number % 3 == 0) {
            sb.append("FOO");
        }
        if(number % 5 == 0) {
            sb.append("BAR");
        }
        
        char[] array = number.toString().toCharArray();
        for(int i=0; i<array.length; i++) {
            char c = array[i];
            if(c == '3') {
                sb.append("FOO");
          }
          else if(c == '5') {
                sb.append("BAR");
          }
          else if(c == '7') {
              sb.append("QUIX");
        }
        }
       
    String result = sb.toString();
    if(result.isEmpty()) {
        result = number.toString();
    }
     return result;
    }
    


  

}
