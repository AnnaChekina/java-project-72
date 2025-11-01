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

                        <div class="row">
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
                    </div>
                </div>
            </div>
        </div>
    `,
    page = page
)
