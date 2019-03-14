package com.nikonova.org;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;


import static com.codeborne.selenide.Selenide.*;

public class ExersizeTest {

    public PropertyManager pm = new PropertyManager();
    StopWatch stopWatch = new StopWatch();

    public float TotalSum = 0;
    public float SumCanceledLot = 0;
    public int TotalLotsCount = 0;
    @Test
    public void Test223() {
        stopWatch.start();
        String result = "";
        result += "Начало теста Test223";
        result += "\n страница поиска закупок https://223.rts-tender.ru/supplier/auction/Trade/Search.aspx";
        open(pm.GetData("trade_search_url"));

        result += "\n Даты публикации извещения";
        $(pm.GetData("publicationDateFromSelector")).setValue(pm.GetData("publicationDateFrom"));
        $(pm.GetData("publicationDateToSelector")).setValue(pm.GetData("publicationDateTo"));

        result += "\n Чек-бокс Закупка в соответствии с нормами 223-ФЗ (за исключением норм статьи 3.2 223-ФЗ)";
        $("#BaseMainContent_MainContent_chkPurchaseType_0").click();;

        result += "\n //Чек-бокс Коммерческая закупка";
        $("#BaseMainContent_MainContent_chkPurchaseType_1").click();;

        result += "\n //Начальная цена от 0";
        $("#BaseMainContent_MainContent_txtStartPrice_txtRangeFrom").setValue("0");

        result += "\n //Производим поиск";
        $("#BaseMainContent_MainContent_btnSearch").click();

        result += "\n Вызван метод GetEISValue";
        sleep(2000);
        GetEISValue();

        System.out.println("Сумма: " + String.format("%.0f",TotalSum) + "\t" + "Кол-во лотов: " + TotalLotsCount);
        System.out.println("Сумма лотов без лотов со статусом \"Отменен\": " + String.format("%.0f",TotalSum - SumCanceledLot));

        result += "\n Сумма: \" + String.format(\"%.0f\",TotalSum) + \"\\t\" + \"Кол-во лотов: \" + TotalLotsCount";
        result += "Сумма лотов без лотов со статусом \"Отменен\": " + String.format("%.0f",TotalSum - SumCanceledLot);
        stopWatch.stop();
        result += "Конец теста Test223 время потраченное на тест:" + stopWatch.getTime();

        PrintResult(result);

    }

    private void Sum(String lotPrice, boolean isCanceled){
        lotPrice = lotPrice.replace("руб.", "");
        lotPrice = lotPrice.replace(" ", "");
        lotPrice = lotPrice.replace(",", ".");
        try{
            if(isCanceled){
                SumCanceledLot += Float.parseFloat(lotPrice);
            }
            TotalSum += Float.parseFloat(lotPrice);
        }

        catch (Exception ex)
        {

        }
        finally {
            TotalLotsCount++;
        }
    }
    public void GetEISValue() {
        WebElement table = $("#BaseMainContent_MainContent_jqgTrade");
        WebElement tableBody = table.findElement(By.tagName("tbody"));
        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        for (WebElement row : rows) {
            try{
                WebElement oosNumber = row.findElement(By.xpath("//*[@aria-describedby=\"BaseMainContent_MainContent_jqgTrade_OosNumber\"]"));
                WebElement lotPrice = row.findElement(By.xpath("//*[@aria-describedby=\"BaseMainContent_MainContent_jqgTrade_StartPrice\"]"));
                if(lotPrice.getText().length() > 0 && oosNumber.getText().length() > 0)
                {
                    Sum(lotPrice.getText(), false);
                }
            }
            catch(Exception ex)
            {
                continue;
            }
        }

        List<WebElement> сancelLots =
                tableBody.findElements(By.xpath(
                        "//*[@aria-describedby=\"BaseMainContent_MainContent_jqgTrade_LotStateString\" and contains(.,\"Отменена\")]"));
        for(WebElement сancelLot : сancelLots)
        {
            WebElement lotPrice = сancelLot.findElement(By.xpath("//*[@aria-describedby=\"BaseMainContent_MainContent_jqgTrade_StartPrice\"]"));
            if(lotPrice.getText().length() > 0)
            {
                Sum(lotPrice.getText(), true);
            }
        }
    }

    public void PrintResult(String result)
    {
        try(FileWriter writer = new FileWriter("result.txt", false))
        {
            writer.write(result);
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }
}
