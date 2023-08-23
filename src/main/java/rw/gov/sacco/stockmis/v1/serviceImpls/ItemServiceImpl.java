package rw.gov.sacco.stockmis.v1.serviceImpls;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.gov.sacco.stockmis.v1.dtos.CreateOrUpdateItemDTO;
import rw.gov.sacco.stockmis.v1.dtos.ReportRecordDTO;
import rw.gov.sacco.stockmis.v1.enums.EStockStatus;
import rw.gov.sacco.stockmis.v1.exceptions.BadRequestException;
import rw.gov.sacco.stockmis.v1.exceptions.ResourceNotFoundException;
import rw.gov.sacco.stockmis.v1.models.*;
import rw.gov.sacco.stockmis.v1.repositories.IItemRecordRepository;
import rw.gov.sacco.stockmis.v1.repositories.IItemRepository;
import rw.gov.sacco.stockmis.v1.repositories.IItemTraceRepository;


import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rw.gov.sacco.stockmis.v1.services.*;


@Service
public class ItemServiceImpl implements IItemService {

    private final IItemRepository itemRepository;

    private final IItemRecordRepository itemRecordRepository;
    private final IItemCategoryService itemCategoryService;

    private final IItemEntryService itemEntryService;

    private final IRequestService requestService;

    private final IItemTraceRepository itemTraceRepository;

    private final IUserService userService;



    @Autowired
    public ItemServiceImpl(IItemRepository itemRepository, IItemCategoryService itemCategoryService, IItemRecordRepository itemRecordRepository, @Lazy IItemEntryService itemEntryService, @Lazy IRequestService requestService, IItemTraceRepository itemTraceRepository, @Lazy IUserService userService) {
        this.itemRepository = itemRepository;
        this.itemCategoryService = itemCategoryService;
        this.itemRecordRepository = itemRecordRepository;
        this.itemEntryService = itemEntryService;
        this.requestService = requestService;
        this.itemTraceRepository = itemTraceRepository;
        this.userService = userService;
    }

    @Override
    public boolean isValidMonth(String startMonth) {
        try {
            Month.valueOf(startMonth.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public void createManyItems(String jsonData, String itemCategoryName) {
        JSONArray jsonArray = new JSONArray(jsonData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String name = jsonObject.getString("name");
            String number = jsonObject.getString("number");
            String price = jsonObject.getString("price");

            Item item = new Item();
            item.setName(name);
            item.setNbrOfItemsAvailable(Integer.parseInt(number));
            item.setPrice(Double.parseDouble(price));
            item.setItemCategory(itemCategoryService.findByName(itemCategoryName));
            item.setOverstockParameter(100);
            item.setUnderstockParameter(10);

            if (item.getNbrOfItemsAvailable() > item.getOverstockParameter()) {
                item.setStockStatus(EStockStatus.OVERSTOCKED);
            } else if (item.getNbrOfItemsAvailable() < item.getUnderstockParameter() && item.getNbrOfItemsAvailable() > 0) {
                item.setStockStatus(EStockStatus.UNDERSTOCKED);
            } else if(item.getNbrOfItemsAvailable() >= item.getUnderstockParameter()){
                item.setStockStatus(EStockStatus.AVAILABLE);
            } else if(item.getNbrOfItemsAvailable() == 0){
                item.setStockStatus(EStockStatus.OUT_OF_STOCK);
            }

            item.setInStockDuration(12);
            itemRepository.save(item);
        }

    }

    @Override
    public int getLastDayOfMonth(String endMonth) {
        return Year.now().atMonth(Month.valueOf(endMonth.toUpperCase())).lengthOfMonth();
    }

    @Override
    public long count() {
        return itemRepository.count();
    }

    @Override
    public Item create(CreateOrUpdateItemDTO itemDTO) {
        if (itemRepository.existsByName(itemDTO.getName())) {
            throw new BadRequestException(String.format("Item with name %s already exists", itemDTO.getName()));
        }

        ItemCategory itemCategory = itemCategoryService.findById(itemDTO.getItemCategoryId());

        // overstock parameter must be greater than understock parameter
        if (itemDTO.getOverstockParameter() <= itemDTO.getUnderstockParameter()) {
            throw new BadRequestException("Overstock parameter must be greater than understock parameter");
        }

        Item item = new Item(itemDTO.getName(), itemCategory, itemDTO.getPrice(), 0, itemDTO.getOverstockParameter(), itemDTO.getUnderstockParameter(), itemDTO.getInStockDuration(), EStockStatus.OUT_OF_STOCK, itemDTO.getItemType());

        return itemRepository.save(item);
    }





    @Override
    public Item findById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id.toString()));
    }

    @Override
    public boolean existsById(UUID id) {
        return itemRepository.existsById(id);
    }

    @Override
    public Iterable<Item> findAll() {
        return null;
    }

    @Override
    public Page<Item> findAll(UUID categoryId, Pageable pageable) {
        if(categoryId == null){
            return itemRepository.findAll(pageable);
        }else {
            return itemRepository.findAllByItemCategory_Id(categoryId,pageable);
        }
    }

    @Override
    public Page<Item> findAll(UUID categoryId, EStockStatus status, String name, Pageable pageable) {
        if(categoryId == null && status == null && (name == null || name.isEmpty())){
            return itemRepository.findAllByStockStatusNot(EStockStatus.DELETED, pageable);
        }else if(categoryId != null && status == null && name == null){
            return itemRepository.findAllByItemCategory_Id(categoryId,pageable);
        }else if(categoryId == null && status != null && name == null){
            return itemRepository.findAllByStockStatus(status,pageable);
        }else if(categoryId == null && status == null && (name != null || !name.isEmpty())){
            return itemRepository.findAllByNameContainingIgnoreCase(name,pageable);
        }else if(categoryId != null && status != null && name == null){
            return itemRepository.findAllByItemCategory_IdAndStockStatus(categoryId,status,pageable);
        }else if(categoryId != null && status == null && name != null){
            return itemRepository.findAllByItemCategory_IdAndNameContainingIgnoreCase(categoryId,name,pageable);
        }else if(categoryId == null && status != null && name != null){
            return itemRepository.findAllByStockStatusAndNameContainingIgnoreCase(status,name,pageable);
        }else if(categoryId != null && status != null && name != null){
            return itemRepository.findAllByItemCategory_IdAndStockStatusAndNameContainingIgnoreCase(categoryId,status,name,pageable);
        }

        return itemRepository.findAllByStockStatusNot(EStockStatus.DELETED, pageable);
    }


    @Override
    public Page<Item> findAllActive(Pageable pageable) {
        return itemRepository.findAllByStockStatus(EStockStatus.AVAILABLE, pageable);
    }

    @Override
    @Transactional
    public Item update(UUID id, CreateOrUpdateItemDTO itemDTO, String reason) {
        Item existingItem = findById(id);
        ItemCategory itemCategory = itemCategoryService.findById(itemDTO.getItemCategoryId());

        EStockStatus newStockStatus = existingItem.getStockStatus();
        if (existingItem.getNbrOfItemsAvailable() > itemDTO.getOverstockParameter()) {
            newStockStatus = EStockStatus.OVERSTOCKED;
        } else if (existingItem.getNbrOfItemsAvailable() < itemDTO.getUnderstockParameter() && existingItem.getNbrOfItemsAvailable() > 0) {
            newStockStatus = EStockStatus.UNDERSTOCKED;
        } else if(existingItem.getNbrOfItemsAvailable() >= itemDTO.getUnderstockParameter()){
            newStockStatus = EStockStatus.AVAILABLE;
        } else if(existingItem.getNbrOfItemsAvailable() == 0){
            newStockStatus = EStockStatus.OUT_OF_STOCK;
        }

        ItemTrace itemTrace = new ItemTrace(existingItem, itemDTO, itemCategory, newStockStatus, reason);
        itemTraceRepository.save(itemTrace);


        if (itemRepository.existsByName(itemDTO.getName()) && (!existingItem.getName().equals(itemDTO.getName()))) {
            throw new BadRequestException(String.format("Item with name %s already exists", itemDTO.getName()));
        }


        // overstock parameter must be greater than understock parameter
        if (itemDTO.getOverstockParameter() <= itemDTO.getUnderstockParameter()) {
            throw new BadRequestException("Overstock parameter must be greater than understock parameter");
        }


        existingItem.setName(itemDTO.getName());
        existingItem.setItemCategory(itemCategory);
        existingItem.setPrice(itemDTO.getPrice());
        existingItem.setOverstockParameter(itemDTO.getOverstockParameter());
        existingItem.setUnderstockParameter(itemDTO.getUnderstockParameter());
        existingItem.setInStockDuration(itemDTO.getInStockDuration());

        if (existingItem.getNbrOfItemsAvailable() > existingItem.getOverstockParameter()) {
            existingItem.setStockStatus(EStockStatus.OVERSTOCKED);
        } else if (existingItem.getNbrOfItemsAvailable() < existingItem.getUnderstockParameter() && existingItem.getNbrOfItemsAvailable() > 0) {
            existingItem.setStockStatus(EStockStatus.UNDERSTOCKED);
        } else if(existingItem.getNbrOfItemsAvailable() >= existingItem.getUnderstockParameter()){
            existingItem.setStockStatus(EStockStatus.AVAILABLE);
        } else if(existingItem.getNbrOfItemsAvailable() == 0){
            existingItem.setStockStatus(EStockStatus.OUT_OF_STOCK);
        }

        return itemRepository.save(existingItem);
    }

    @Override
    @Transactional
    public boolean delete(UUID id, String reason) {
        Item item = findById(id);
        ItemTrace itemTrace = new ItemTrace(item, EStockStatus.DELETED, reason);
        itemTraceRepository.save(itemTrace);
        item.setStockStatus(EStockStatus.DELETED);
        itemRepository.save(item);
        return true;
    }


    @Override
    public Item incrementNbrOfItemsAvailable(UUID id, int nbrOfItemsToIncrement) {
        Item item = findById(id);
        item.setNbrOfItemsAvailable(item.getNbrOfItemsAvailable() + nbrOfItemsToIncrement);

        // change stock status if necessary
        if (item.getNbrOfItemsAvailable() > item.getOverstockParameter()) {
            item.setStockStatus(EStockStatus.OVERSTOCKED);
        } else if (item.getNbrOfItemsAvailable() < item.getUnderstockParameter() && item.getNbrOfItemsAvailable() > 0) {
            item.setStockStatus(EStockStatus.UNDERSTOCKED);
        } else if(item.getNbrOfItemsAvailable() >= item.getUnderstockParameter()){
            item.setStockStatus(EStockStatus.AVAILABLE);
        } else if(item.getNbrOfItemsAvailable() == 0){
            item.setStockStatus(EStockStatus.OUT_OF_STOCK);
        }

        return itemRepository.save(item);
    }

    @Override
    public Item decrementNbrOfItemsAvailable(UUID id, int nbrOfItemsToDecrement) {
        Item item = findById(id);
        item.setNbrOfItemsAvailable(item.getNbrOfItemsAvailable() - nbrOfItemsToDecrement);

        // change stock status if necessary
        if (item.getNbrOfItemsAvailable() > item.getOverstockParameter()) {
            item.setStockStatus(EStockStatus.OVERSTOCKED);
        } else if (item.getNbrOfItemsAvailable() < item.getUnderstockParameter() && item.getNbrOfItemsAvailable() > 0) {
            item.setStockStatus(EStockStatus.UNDERSTOCKED);
        } else if(item.getNbrOfItemsAvailable() >= item.getUnderstockParameter()){
            item.setStockStatus(EStockStatus.AVAILABLE);
        } else if(item.getNbrOfItemsAvailable() == 0){
            item.setStockStatus(EStockStatus.OUT_OF_STOCK);
        }

        return itemRepository.save(item);
    }

    @Override
    @Scheduled(cron = "0 0 0 1 * ?")
    public void saveItemRecordStartMonth() {
        System.out.println("========================");
        System.out.println("saveItemRecordStartMonth");
        System.out.println("========================");
        List<Item> items = itemRepository.findAll();
        for (Item item : items) {
            ItemRecord itemRecord = new ItemRecord(item);
            itemRecordRepository.save(itemRecord);
        }
    }

    @Override
    @Scheduled(cron = "0 59 23 28-31 * ?")
    public void saveItemRecordEndMonth() {
        System.out.println("========================");
        System.out.println("saveItemRecordEndMonth");
        System.out.println("========================");
        List<Item> items = itemRepository.findAll();
        for (Item item : items) {
            ItemRecord itemRecord = new ItemRecord(item);
            itemRecordRepository.save(itemRecord);
        }
    }

    @Override
    public Boolean forceSaveItemRecord(LocalDate date) {
        System.out.println("========================");
        System.out.println("forceSaveItemRecord");
        System.out.println("========================");
        List<Item> items = itemRepository.findAll();
        for (Item item : items) {
            ItemRecord itemRecord = new ItemRecord(item, date);
            itemRecordRepository.save(itemRecord);
        }

        return true;
    }

    @Override
    public List<ReportRecordDTO> getMonthlyReports(String categoryName, String startMonth, String endMonth, int year) {

        List<ReportRecordDTO> reportRecordDTOS = new ArrayList<>();

        //check if category with name exists
        ItemCategory itemCategory = itemCategoryService.findByName(categoryName);
        if(itemCategory == null){
            throw new BadRequestException(String.format("Category with name %s does not exist", categoryName));
        }

        /**** DEALING WITH DATES ****/

        // get Month from startMonth and endMonth
        Month startMonthEnum = Month.valueOf(startMonth.toUpperCase());
        Month endMonthEnum = Month.valueOf(endMonth.toUpperCase());

        // get start date from startMonth and year
        LocalDate startDate = LocalDate.of(year, startMonthEnum, 1);

        // get end date from endMonth and year
        // calculate the last day of the month considering possibility of leap year
        int lastDayOfMonth = endMonthEnum.length(Year.isLeap(year));
        LocalDate endDate = LocalDate.of(year, endMonthEnum, lastDayOfMonth);

        /**** DEALING WITH DATES ****/
        //  for ()

        // get all items in ItemCategory
        List<Item> items = itemRepository.findAllByItemCategory(itemCategory);

        for(Item item: items){
//            ItemRecord startMonthRecord = itemRecordRepository.findByNameAndDateOfRecord(item.getName(), startDate).orElseThrow(() -> new BadRequestException(String.format("No record found for category %s in month %s", categoryName, startMonth)));
//            ItemRecord endMonthRecord = itemRecordRepository.findByNameAndDateOfRecord(item.getName(), endDate).orElseThrow(() -> new BadRequestException(String.format("No record found for category %s in month %s", categoryName, endMonth)));
            ItemRecord startMonthRecord;
            ItemRecord endMonthRecord;

            System.out.println("===================================");
            System.out.println("item name: " + item.getName());
            System.out.println("start date: " + startDate);
            System.out.println("end date: " + endDate);
            System.out.println("===================================");

            if(itemRecordRepository.findByNameAndDateOfRecord(item.getName(), startDate).isPresent() && itemRecordRepository.findByNameAndDateOfRecord(item.getName(), endDate).isPresent()){
                startMonthRecord = itemRecordRepository.findByNameAndDateOfRecord(item.getName(), startDate).get();
                endMonthRecord = itemRecordRepository.findByNameAndDateOfRecord(item.getName(), endDate).get();
                System.out.println("===================================");
                System.out.println("Data found: " + startMonthRecord.getNbrOfItemsAvailable() + " " + endMonthRecord.getNbrOfItemsAvailable());
                System.out.println("===================================");
            }
            else {
                continue;
            }

            ReportRecordDTO reportRecordDTO = new ReportRecordDTO();
            reportRecordDTO.setItemName(item.getName());
            reportRecordDTO.setOpeningBalance(Integer.valueOf(String.valueOf(startMonthRecord.getNbrOfItemsAvailable())));
            reportRecordDTO.setEntrees(Integer.valueOf(String.valueOf(getTotalEntriesBetweenDates(item, startDate, endDate))));
            reportRecordDTO.setStockOut(Integer.valueOf(String.valueOf(getTotalStockOutBetweenDates(item, startDate, endDate))));
            reportRecordDTO.setClosingBalance(Integer.valueOf(String.valueOf(endMonthRecord.getNbrOfItemsAvailable())));
            reportRecordDTO.setUnitPrice(Double.valueOf(String.valueOf(endMonthRecord.getPrice())));
            reportRecordDTO.setAmountIn(Double.valueOf(String.valueOf(Double.valueOf(reportRecordDTO.getEntrees()) * Double.valueOf(reportRecordDTO.getUnitPrice()))));
            reportRecordDTO.setAmountOut(Double.valueOf(String.valueOf(Double.valueOf(reportRecordDTO.getStockOut()) * Double.valueOf(reportRecordDTO.getUnitPrice()))));
            reportRecordDTO.setTotalValueInStock(Double.valueOf(String.valueOf(Double.valueOf(reportRecordDTO.getClosingBalance()) * Double.valueOf(reportRecordDTO.getUnitPrice()))));

            reportRecordDTOS.add(reportRecordDTO);
        }


        System.out.println("===================================");
        System.out.println("reportRecordDTOS: " + reportRecordDTOS.toString());
        System.out.println("===================================");

        return reportRecordDTOS;
    }

    //function to get total entries in a month for an item
    private int getTotalEntriesBetweenDates(Item item, LocalDate startDate, LocalDate endDate){
        List<ItemEntry> itemEntries = itemEntryService.findAllByItemAndDateOfPurchaseBetween(item, startDate, endDate);
        System.out.println("===================================");
        System.out.println("itemEntries: " + itemEntries.toString());
        System.out.println("===================================");
        int totalEntries = 0;
        for(ItemEntry itemEntry: itemEntries){
            totalEntries += itemEntry.getQuantity();
        }
        return totalEntries;
    }

    //function to get total stock out in a month for an item
    private int getTotalStockOutBetweenDates(Item item, LocalDate startDate, LocalDate endDate){
        List<Request> requests = requestService.findAllByItemAndDateOfStatusChangeStoreKeeperGrantedBetween(item, startDate, endDate);
        System.out.println("===================================");
        System.out.println("requests: " + requests.toString());
        System.out.println("===================================");

        int totalStockOut = 0;
        for(Request request: requests){
            totalStockOut += request.getSuggestedQuantity();
        }
        return totalStockOut;
    }
}

