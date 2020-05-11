package com.mspark.newjava.java9.completablefuture;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class CompletableFutureImprovements {
	private static ScheduledExecutorService delayer = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
		
		FlightRoutePriceFinder flightRoutePriceFinder = new FlightRoutePriceFinder();
		ExchangeService exchangeService = new ExchangeService();
		
		/**
		 * Java8 부터 출시된 CompletableFuture 클래스로 두개의 서비스를 호출하여 그 결과를 합치는 간단한 코드를 작성해 보았다.
		 *
		 * 각각의 서비스는 랜덤한 지연시간 후에 값을 리턴하도록 작성 하였다. 지연시간은 네트워크 연결에 따른 비용을 가정한것이다.
		 * 
		 * thenCombine() 메서드를 첫번째 비동기 요청의 결과 값과 두번째요청의 결과값을 결합할 수 있다.
		 * 결합된 결고값에 대해 get() 메서드를 통해 결과값을 가져올 수 있다.
		 */
//		BigDecimal amount =
//			    CompletableFuture.supplyAsync(() -> flightRoutePriceFinder.bestFor(AirportCode.LCY, AirportCode.JFK))
//			    				 .thenCombine(CompletableFuture.supplyAsync(() -> exchangeService.rateFor(Currency.GBP)),
//			    						 	  CompletableFutureImprovements::convert)
//			    				 .get(1, TimeUnit.SECONDS);
//		
//		
//		System.out.printf("The price is %s %s", amount, Currency.GBP);
		
		/**
		 * 위의 코드에는 몇가지 문제가 있다. 
		 * 1. get() 메서드는 blocking call로 동작한다. 즉 메인스레드에서 실해되는 get() 메서드는 비동기 스레드에서 값이 넘어 올 때 까지 기다려야 한다는 것이다.
		 * 2. 비동기 스레드에서 부하가 있거나 응답이 없는 경우 메인스레드가 무기한으로 block 될 수 있다. get() 메서드에  타입아웃 시간을 설정하여 특정 시간 이후에 예외를 발생 시킬 수 있다.
		 * 
		 * 하지만 여전히 위의 코드는 blocking call 로 동작한다. 이를 non blocking call로 바꾸기 위해 get() 메서드 대신 thenAccept() 또는 acceptEither()를 통해 해결 할 수 있다.
		 * Timeout 처리를 위해 새로운 CompltableFuture를 반환하고 시간을 초과하면 예외를 뱉도록 작성한다
		 * 
		 */

	    CompletableFuture.supplyAsync(() -> flightRoutePriceFinder.bestFor(AirportCode.LCY, AirportCode.JFK))
	    				 .thenCombine(CompletableFuture.supplyAsync(() -> exchangeService.rateFor(Currency.GBP)),
	    						 	  CompletableFutureImprovements::convert)
	    				 .acceptEither(timeoutAfter(1, TimeUnit.SECONDS), amount -> System.out.printf("The price is %s %s", amount, Currency.GBP));
		
	    System.out.println("Is it first call??? then It's Noblocking call!!!");
	    
	    /**
	     * Java9에서는 Timeout을 처리하기 위한 메서드들이 추가되었다.
	     * orTimeout()은 수행시간 초과시 새로운 CompletableFuture를 리턴하는데 이는 개발자로 하여금 파이프라인 체이닝을 가능하게 해준다.
	     * whenComplete() 메서드는 Timeout 예외 보다 이전에 다른 예외가 발생한다면 그 예외를 보고한다.
	     */
	    CompletableFuture.supplyAsync(() -> flightRoutePriceFinder.bestFor(AirportCode.LCY, AirportCode.JFK))
						 .thenCombine(CompletableFuture.supplyAsync(() -> exchangeService.rateFor(Currency.GBP)),
								 	  CompletableFutureImprovements::convert)
//						 .orTimeout(1, TimeUnit.MILLISECONDS)
						 .orTimeout(1, TimeUnit.SECONDS)
						 .whenComplete((amount, error) ->{
						        if (error == null) {
						            System.out.printf("The price is %s %s", amount, Currency.GBP);
						        } else {
						            System.out.println("Sorry, something unexpected happened: " + error);
						        }
						 });
	    
	    /**
	     * completeOnTimeout() 메서드는 수행시간 초과 시 주어진 디폴트 값을 반환하고 파이프 라인을 종료한다.
	     */
	    BigDecimal DEFAULT_VALUE = new BigDecimal(100);
	    CompletableFuture.supplyAsync(() -> flightRoutePriceFinder.bestFor(AirportCode.LCY, AirportCode.JFK))
						 .thenCombine(CompletableFuture.supplyAsync(() -> exchangeService.rateFor(Currency.GBP)),
								 	  CompletableFutureImprovements::convert)
						 .completeOnTimeout(DEFAULT_VALUE, 1, TimeUnit.SECONDS)
						 .thenAccept(amount ->System.out.printf("The price is %s %s", amount, Currency.GBP));
	    
		
	}
	
    private static <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
    	CompletableFuture<T> future = new CompletableFuture<T>();
        delayer.schedule(() -> future.completeExceptionally(new TimeoutException()), timeout, unit);
        return future;
    }

    private static BigDecimal convert(BigDecimal price, BigDecimal rate) {
        return Utils.decimal(price.multiply(rate));
    }
}


class FlightRoutePriceFinder {
    BigDecimal bestFor(AirportCode departure, AirportCode destination) {
        return bestPriceWithDelay(departure, destination);
    }

    private BigDecimal bestPriceWithDelay(AirportCode departure, AirportCode destination) {
        return new Delay<BigDecimal>().random()
            .then(() -> {
                double price = 10 * Utils.randomChar(departure.getName()) + Utils.randomChar(destination.getName());
                return Utils.decimal(price);
            });
    }
}

enum AirportCode {
    LCY("London City Airport"), JFK("John F. Kennedy International Airport");

    private final String name;

    AirportCode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

enum Currency {
    USD(1.0), GBP(0.769375);

    public final BigDecimal rate;

    Currency(Double rate) {
        this.rate = new BigDecimal(rate.toString());
    }
}

class ExchangeService {
    BigDecimal rateFor(Currency currency) {
        return rateWithDelayFor(currency);
    }

    private BigDecimal rateWithDelayFor(Currency currency) {
        return new Delay<BigDecimal>().random()
            .then(() -> currency.rate);
    }
}

class Delay<T> {
    private static final int MIN_DELAY_IN_MS = 750;
    private static final int MAX_DELAY_IN_MS = 1000;

    Delay<T> random() {
        int delayInMs = ThreadLocalRandom.current().nextInt(MIN_DELAY_IN_MS, MAX_DELAY_IN_MS);
        try {
            Thread.sleep(delayInMs);
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
        return this;
    }

    T then(Supplier<T> supplier) {
        return supplier.get();
    }
}

class Utils {
    private static final int SCALE = 2;

    static char randomChar(String value) {
        int randomInt = new Random().nextInt(value.length());
        return value.charAt(randomInt);
    }

    static BigDecimal decimal(double value) {
    	BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(SCALE, RoundingMode.HALF_UP);
        return bigDecimal;
    }

    static BigDecimal decimal(BigDecimal value) {
        return decimal(value.doubleValue());
    }
}