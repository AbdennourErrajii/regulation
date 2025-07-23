package s2m.me.regulation.steps.settlementfinalization;


import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;

import java.util.ArrayList;
import java.util.Set;

public class DistinctCurrencyReader implements ItemReader<String> {
    private final IteratorItemReader<String> delegateReader;

    public DistinctCurrencyReader(Set<String> distinctCurrencies) {
        this.delegateReader = new IteratorItemReader<>(new ArrayList<>(distinctCurrencies));
    }

    @Override
    public String read() throws Exception {
        return delegateReader.read();
    }
}