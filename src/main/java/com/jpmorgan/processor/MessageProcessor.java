package com.jpmorgan.processor;

import com.jpmorgan.processor.model.Statistics;

public interface MessageProcessor {

    Statistics process(String message);

}
