package hvpaiva.query.repository

import hvpaiva.query.entity.PlanningEntity
import org.springframework.data.repository.PagingAndSortingRepository

interface PlanningRepository : PagingAndSortingRepository<PlanningEntity, String>