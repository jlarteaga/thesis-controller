package dev.jlarteaga.coordinator.webclient.dto.text;

public interface GetTextMetaDetailed extends GetTextDetailed {

    String getParent();

    void setParent(String parent);

    String getParentType();

    void setParentType(String parentType);

}
