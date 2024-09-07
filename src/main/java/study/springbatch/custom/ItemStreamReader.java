package study.springbatch.custom;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;

public interface ItemStreamReader<T> extends ItemStream, ItemReader<T> {}
