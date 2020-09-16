package top.doudou.commons.entity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.doudou.commons.utils.ListUtils;
import io.swagger.annotations.ApiModel;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * @author chensen
 * @create 2018-09-30-10:56
 */
@ApiModel("分页模型")
public class PageModel<T> implements Serializable {

    public PageModel(Long count, List<T> results) {
        this.count = count;
        this.results = results;
    }

    @SuppressWarnings("unchecked")
    public PageModel(Page page) {
        this.count = page.getTotalElements();
        this.results = page.getContent();
    }

    public PageModel(Page page, Class<T> target) {
        this.count = page.getTotalElements();
        this.results = ListUtils.copyList(page.getContent(), target);
    }

    public <E>PageModel(IPage<E> page,Class<T> target){
        this.results = ListUtils.copyList(page.getRecords(), target);
        this.count = page.getTotal();
    }


    private long count;
    private List<T> results;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
