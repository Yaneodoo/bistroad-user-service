package kr.bistroad.userservice.global.config.swagger

import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Pageable
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors.any
import springfox.documentation.builders.RequestHandlerSelectors.basePackage
import springfox.documentation.schema.AlternateTypeRules.newRule
import springfox.documentation.service.Tag
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig(
    private val typeResolver: TypeResolver,

    @Value("\${swagger.host}")
    private val host: String
) {
    @Bean
    fun swaggerDocket() = Docket(DocumentationType.OAS_30)
        .host(host)
        .apiInfo(
            ApiInfoBuilder().title("User API").build()
        )
        .tags(Tag("/users", "User API"))
        .alternateTypeRules(
            newRule(
                typeResolver.resolve(Pageable::class.java),
                typeResolver.resolve(Page::class.java)
            )
        )
        .select()
        .apis(basePackage("org.springframework.boot").negate())
        .paths(any())
        .build()!!

    data class Page(
        @ApiModelProperty("\${swagger.doc.model.page.page.description}")
        val page: Int,

        @ApiModelProperty("\${swagger.doc.model.page.size.description}")
        val size: Int,

        @ApiModelProperty("\${swagger.doc.model.page.sort.description}")
        val sort: List<String>
    )
}