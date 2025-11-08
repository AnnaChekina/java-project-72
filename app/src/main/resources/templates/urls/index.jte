@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
    content = @`
        <div class="row justify-content-center">
            <div class="col-md-10">
                <div class="card shadow">
                    <div class="card-body p-5">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h1 class="card-title mb-0">Сайты</h1>
                            <a href="${NamedRoutes.rootPath()}" class="btn btn-outline-primary">+ Добавить</a>
                        </div>

                        @if(page.getUrls().isEmpty())
                            <div class="text-center py-4">
                                <p class="text-muted">Пока не добавлено ни одного сайта</p>
                                <a href="${NamedRoutes.rootPath()}" class="btn btn-primary">Добавить первый сайт</a>
                            </div>
                        @else
                            <div class="table-responsive">
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Имя</th>
                                            <th>Последняя проверка</th>
                                            <th>Код ответа</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        @for(var urlWithCheck : page.getUrlWithChecks())
                                            <tr>
                                                <td>${urlWithCheck.getUrl().getId()}</td>
                                                <td>
                                                    <a href="${NamedRoutes.urlPath(urlWithCheck.getUrl().getId())}">${urlWithCheck.getUrl().getName()}</a>
                                                </td>
                                                <td>${urlWithCheck.getLastCheckDate()}</td>
                                                <td>
                                                    @if(urlWithCheck.getLastStatusCode() != null)
                                                        ${urlWithCheck.getLastStatusCode()}
                                                    @else
                                                        <span class="text-muted">—</span>
                                                    @endif
                                                </td>
                                            </tr>
                                        @endfor
                                    </tbody>
                                </table>
                            </div>
                        @endif
                    </div>
                </div>
            </div>
        </div>
    `,
    page = page
)
