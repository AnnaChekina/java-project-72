@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes
@param UrlPage page

@template.layout.page(
    content = @`
        <div class="row justify-content-center">
            <div class="col-md-10">
                <div class="card shadow">
                    <div class="card-body p-5">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h1 class="card-title mb-0">Сайт: ${page.getUrl().getName()}</h1>
                            <div>
                                <a href="${NamedRoutes.urlsPath()}" class="btn btn-outline-secondary">← К списку</a>
                            </div>
                        </div>

                        <div class="row mb-4">
                            <div class="col-md-6">
                                <div class="card">
                                    <div class="card-header">
                                        <h5 class="card-title mb-0">Информация о сайте</h5>
                                    </div>
                                    <div class="card-body">
                                        <p><strong>ID:</strong> ${page.getUrl().getId()}</p>
                                        <p><strong>URL:</strong> ${page.getUrl().getName()}</p>
                                        <p><strong>Дата создания:</strong> ${page.getUrl().getFormattedCreatedAt()}</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h2>Проверки</h2>
                            <form action="${NamedRoutes.checkPath(page.getUrl().getId())}" method="POST">
                                <button type="submit" class="btn btn-primary">Запустить проверку</button>
                            </form>
                        </div>

                        @if(page.getChecks().isEmpty())
                            <div class="text-center py-4">
                                <p class="text-muted">Проверок еще не было</p>
                            </div>
                        @else
                            <div class="table-responsive">
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Код ответа</th>
                                            <th>Заголовок</th>
                                            <th>H1</th>
                                            <th>Description</th>
                                            <th>Дата проверки</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        @for(var check : page.getChecks())
                                            <tr>
                                                <td>${check.getId()}</td>
                                                <td>${check.getStatusCode()}</td>
                                                <td>${check.getTitle() != null ? check.getTitle() : ""}</td>
                                                <td>${check.getH1() != null ? check.getH1() : ""}</td>
                                                <td>
                                                    @if(check.getDescription() != null && check.getDescription().length() > 50)
                                                        ${check.getDescription().substring(0, 50)}...
                                                    @else
                                                        ${check.getDescription() != null ? check.getDescription() : ""}
                                                    @endif
                                                </td>
                                                <td>${check.getFormattedCreatedAt()}</td>
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
