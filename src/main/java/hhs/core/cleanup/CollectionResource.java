package hhs.core.cleanup;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static org.familysearch.homelands.core.svc.ServiceHelper.DATE_TIME_PATTERN;

@JsonRootName(value = "collection")
public class CollectionResource implements Serializable {

    private static final long serialVersionUID = -6837412558616670199L;

    private String               id;
    private String               name;
    private String               description;
    private String               originLanguage;
    private Set<String>          availableLanguages;
    private String               visibility;
    private Set<String>          types;
    private String               attributionURL;
    private Map<String, String>  attribution;
    private Map<String, Integer> priority;
    private String               createUserId;
    private LocalDateTime        createDate;
    private String               modifyUserId;
    private LocalDateTime        modifyDate;
    private String               source;
    private String               partner;
    private String               contractType;
    private LocalDateTime        expirationDate;

    // Must have a default constructor
    public CollectionResource() { }

    @JsonProperty("id")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("originLanguage")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getOriginLanguage() {
        return originLanguage;
    }

    @JsonProperty("originLanguage")
    public void setOriginLanguage(String originLanguage) {
        this.originLanguage = originLanguage;
    }

    @JsonProperty("availableLanguages")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public Set<String> getAvailableLanguages() {
        return availableLanguages;
    }

    @JsonProperty("availableLanguages")
    public void setAvailableLanguages(Set<String> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    @JsonProperty("visibility")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @JsonProperty("types")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public Set<String> getTypes() {
        return types;
    }

    @JsonProperty("types")
    public void setTypes(Set<String> types) {
        this.types = types;
    }

    @JsonProperty("attributionURL")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getAttributionURL() {
        return attributionURL;
    }

    @JsonProperty("attributionURL")
    public void setAttributionURL(String attributionURL) {
        this.attributionURL = attributionURL;
    }

    @JsonProperty("attribution")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public Map<String, String> getAttribution() {
        return attribution;
    }

    @JsonProperty("attribution")
    public void setAttribution(Map<String, String> attribution) {
        this.attribution = attribution;
    }

    @JsonProperty("createUserId")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getCreateUserId() {
        return createUserId;
    }

    @JsonProperty("createUserId")
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    @JsonProperty("createDate")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    @JsonProperty("createDate")
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @JsonProperty("modifyUserId")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getModifyUserId() {
        return modifyUserId;
    }

    @JsonProperty("modifyUserId")
    public void setModifyUserId(String modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    @JsonProperty("modifyDate")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    @JsonProperty("modifyDate")
    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    @JsonProperty("source")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("partner")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getPartner() {
        return partner;
    }

    @JsonProperty("partner")
    public void setPartner(String partner) {
        this.partner = partner;
    }

    @JsonProperty("contractType")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getContractType() {
        return contractType;
    }

    @JsonProperty("contractType")
    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    @JsonProperty("expirationDate")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    @JsonProperty("expirationDate")
    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    @JsonProperty("priority")
    @JsonInclude(Include.NON_NULL)
    public Map<String, Integer> getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Map<String, Integer> priority) {
        this.priority = priority;
    }

}
