package hexlet.code.dto;

import hexlet.code.model.Url;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UrlsPage extends BasePage {
    private List<Url> urls;
    private List<UrlWithCheck> urlWithChecks;

    public UrlsPage(List<Url> urls, List<UrlWithCheck> urlWithChecks) {
        this.urls = urls;
        this.urlWithChecks = urlWithChecks;
    }

    @Getter
    @Setter
    public static class UrlWithCheck {
        private Url url;
        private Integer lastStatusCode;
        private String lastCheckDate;

        public UrlWithCheck(Url url, Integer lastStatusCode, String lastCheckDate) {
            this.url = url;
            this.lastStatusCode = lastStatusCode;
            this.lastCheckDate = lastCheckDate;
        }
    }
}
