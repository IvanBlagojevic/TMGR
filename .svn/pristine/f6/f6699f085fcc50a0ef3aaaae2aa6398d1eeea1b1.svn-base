package org.gs4tr.termmanager.io.tlog.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.gs4tr.termmanager.io.edd.api.EventDispatcher;
import org.gs4tr.termmanager.io.edd.event.UpdateCountEvent;
import org.gs4tr.termmanager.io.exception.EventException;
import org.gs4tr.termmanager.io.exception.TransactionError;
import org.gs4tr.termmanager.io.tlog.TransactionLogIO;
import org.gs4tr.termmanager.io.utils.PropertyUtils;
import org.gs4tr.termmanager.model.TransactionalUnit;
import org.gs4tr.termmanager.model.dto.ProjectDetailsIO;
import org.gs4tr.termmanager.model.dto.StatisticInfoIO;
import org.gs4tr.termmanager.model.dto.converter.ProjectDetailsIOConverter;
import org.gs4tr.termmanager.model.dto.converter.StatisticsInfoIOConverter;
import org.gs4tr.termmanager.model.event.ProjectDetailInfo;
import org.gs4tr.termmanager.model.event.StatisticsInfo;
import org.gs4tr.termmanager.model.glossary.TermEntry;
import org.gs4tr.termmanager.model.serializer.JsonIO;
import org.springframework.stereotype.Component;

import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityId;
import jetbrains.exodus.entitystore.EntityIterable;

@Component("tLogHandler")
public class TransactionLogHandler extends AbstractTransactionLogHandler
	implements TransactionLogIO<EntityId, TransactionalUnit> {

    @Override
    public Optional<EntityId> appendAndLink(long projectId, EntityId parentKey, TransactionalUnit unit)
	    throws TransactionError {
	return computeInExclusiveTransaction(projectId, txn -> {

	    Entity entityBatch = txn.newEntity(PropertyUtils.BATCH);
	    entityBatch.setProperty(PropertyUtils.PROJECT_ID, projectId);

	    Entity entityCount = txn.newEntity(PropertyUtils.INFO);

	    entityBatch.setProperty(PropertyUtils.TIMESTAMP, System.currentTimeMillis());
	    entityCount.setProperty(PropertyUtils.TIMESTAMP, System.currentTimeMillis());

	    try {
		addTermEntriesToEntity(unit.getTermEntries(), entityBatch);
		addProjectDetailInfoToEntity(unit.getProjectDetailInfo(), entityCount);
		addStatisticInfoToEntity(unit.getStatisticsInfo(), entityCount);
	    } catch (Exception e) {
		throw new RuntimeException(e.getMessage(), e);
	    }

	    Entity parentEntity = txn.getEntity(parentKey);
	    parentEntity.addLink(PropertyUtils.BATCH_LINK, entityBatch);
	    parentEntity.addLink(PropertyUtils.INFO_LINK, entityCount);

	    return entityBatch.getId();
	});
    }

    @Override
    public void finishAppending(long projectId, EntityId key) throws TransactionError {
	try {
	    computeInExclusiveTransaction(projectId, txn -> {
		Entity entity = txn.getEntity(key);

		entity.setProperty(PropertyUtils.FINISHED, Boolean.TRUE);
		entity.setProperty(PropertyUtils.FINISH_TIMESTAMP, System.currentTimeMillis());

		Object collectionObject = entity.getProperty(PropertyUtils.COLLECTION);

		String collection = Objects.nonNull(collectionObject) ? collectionObject.toString() : null;

		EntityIterable batchChildIterable = entity.getLinks(PropertyUtils.BATCH_LINK);

		for (Entity child : batchChildIterable) {
		    dispatch(collection, child);
		}

		EntityIterable countChildIterable = entity.getLinks(PropertyUtils.INFO_LINK);

		for (Entity child : countChildIterable) {
		    dispatchCounts(child);
		}

		return entity.getId();
	    });
	} finally {
	    getStoreHandler().closeAndClear(projectId);
	}
    }

    @Override
    public boolean isLocked(long projectId) {
	return getStoreHandler().isLocked(projectId);
    }

    @Override
    public Optional<EntityId> startAppending(long projectId, String user, String action, String collection)
	    throws TransactionError {
	try {
	    getStoreHandler().open(projectId);
	} catch (Exception e) {
	    throw new TransactionError(e.getMessage(), e);
	}

	return computeInExclusiveTransaction(projectId, txn -> {
	    Entity entity = txn.newEntity(String.valueOf(projectId));
	    entity.setProperty(PropertyUtils.PROJECT_ID, projectId);
	    entity.setProperty(PropertyUtils.USER, user);
	    entity.setProperty(PropertyUtils.ACTION, action);
	    entity.setProperty(PropertyUtils.START_TIMESTAMP, System.currentTimeMillis());
	    entity.setProperty(PropertyUtils.FINISHED, Boolean.FALSE);
	    entity.setProperty(PropertyUtils.COLLECTION, collection);
	    return entity.getId();
	});
    }

    private void addProjectDetailInfoToEntity(ProjectDetailInfo info, Entity entity) throws IOException {
	if (Objects.nonNull(info)) {
	    writeBlob(PropertyUtils.COUNT, entity,
		    JsonIO.writeValueAsBytes(ProjectDetailsIOConverter.fromInternalToIo(info)));
	}
    }

    private void addStatisticInfoToEntity(Set<StatisticsInfo> info, Entity entity) throws IOException {
	if (CollectionUtils.isNotEmpty(info)) {
	    writeBlob(PropertyUtils.STATISTICS, entity,
		    JsonIO.writeValueAsBytes(StatisticsInfoIOConverter.fromInternalToIo(info)));
	}
    }

    private void addTermEntriesToEntity(List<TermEntry> entries, Entity entity) throws IOException {
	if (CollectionUtils.isNotEmpty(entries)) {
	    writeBlob(PropertyUtils.BLOB, entity, JsonIO.writeValueAsBytes(entries));
	}
    }

    private void dispatchCounts(Entity entity) {
	try (InputStream count = entity.getBlob(PropertyUtils.COUNT);
		InputStream statistics = entity.getBlob(PropertyUtils.STATISTICS)) {
	    if (Objects.isNull(count) && Objects.isNull(statistics)) {
		LOGGER.info("Unable to dispatch info objects because they are null.");
		return;
	    }

	    ProjectDetailsIO projectDetails = JsonIO.readValue(count, ProjectDetailsIO.class);
	    StatisticInfoIO[] infoDetails = JsonIO.readValue(statistics, StatisticInfoIO[].class);

	    Set<StatisticInfoIO> statisticInfoDetails = new HashSet<>();
	    if (Objects.nonNull(infoDetails)) {
		statisticInfoDetails.addAll(Arrays.asList(infoDetails));
	    }

	    getDispatcher().dispatch(new UpdateCountEvent(projectDetails, statisticInfoDetails));

	} catch (Exception e) {
	    // TODO: write error handling code here
	    throw new EventException(e.getMessage(), e);
	}

    }

    protected EventDispatcher getDispatcher() {
	return super.getDispatcher();
    }

}
