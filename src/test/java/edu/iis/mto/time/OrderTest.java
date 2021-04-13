package edu.iis.mto.time;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Mock
    private Clock mockClock;

    @BeforeEach
    void setUp() throws Exception {}

    @Test
    void exceptionForExpiredOrder() {
        Order order = new Order(mockClock);

        Instant submitInstant = Instant.parse("2020-12-20T20:20:20.00Z");
        Instant confirmedAfterExpiredInstant = submitInstant.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(mockClock.getZone()).thenReturn(ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(submitInstant).thenReturn(confirmedAfterExpiredInstant);

        order.submit();

        try
        {
            order.confirm();
            fail("OrderExpiredException not thrown");
        }
        catch (OrderExpiredException e) {}
    }

    @Test
    void expiredOrderStatusTest() {
        Order order = new Order(mockClock);

        Instant submitInstant = Instant.parse("2020-12-20T20:20:20.00Z");
        Instant confirmedAfterExpiredInstant = submitInstant.plus(Order.VALID_PERIOD_HOURS + 1, ChronoUnit.HOURS);

        when(mockClock.getZone()).thenReturn(ZoneId.systemDefault());
        when(mockClock.instant()).thenReturn(submitInstant).thenReturn(confirmedAfterExpiredInstant);

        order.submit();

        try {
            order.confirm();
        } catch (OrderExpiredException e) {
            assertEquals(order.getOrderState(), Order.State.CANCELLED);
        }
    }

}
