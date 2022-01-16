package dev.jlarteaga.coordinator.utils.freeling;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.core.scheduler.Schedulers;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.HashSet;
import java.util.Set;

public class SynsetExtractor extends DefaultHandler {

    private static final String TOKEN_ELEMENT = "token";
    private static final String WN_ATTRIBUTE = "wn";
    @Getter
    private final Set<String> synsets;

    public SynsetExtractor() {
        super();
        this.synsets = new HashSet<>();
    }

    public static Mono<Set<String>> extractFromString(String text) {
        return Mono.create((MonoSink<Set<String>> sink) -> {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                SAXParser saxParser = factory.newSAXParser();
                SynsetExtractor extractor = new SynsetExtractor();
                // TODO: Find a better way to handle this
                //noinspection BlockingMethodInNonBlockingContext
                saxParser.parse(IOUtils.toInputStream(text), extractor);
                sink.success(extractor.getSynsets());
            } catch (Exception e) {
                sink.error(e);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (TOKEN_ELEMENT.equals(qName) && attributes.getValue(WN_ATTRIBUTE) != null) {
            synsets.add(attributes.getValue(WN_ATTRIBUTE));
        }
    }
}
