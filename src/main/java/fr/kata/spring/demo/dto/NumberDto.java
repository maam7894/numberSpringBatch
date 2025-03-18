package fr.kata.spring.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NumberDto{

    @NotNull
    @Min(0)
    @Max(100)
    private int input;

    private String result;

    public Integer getInput() {
        return input;
    }

    public void setInput(Integer input) {
        this.input = input;
    }

    public String getResult() {
        return "\"" + result + "\"" ;
    }
    public void setResult(String result) {
        this.result = result;
    }         
}
