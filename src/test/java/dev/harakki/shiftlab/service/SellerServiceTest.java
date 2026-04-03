package dev.harakki.shiftlab.service;

import dev.harakki.shiftlab.domain.Seller;
import dev.harakki.shiftlab.dto.SellerCreateDto;
import dev.harakki.shiftlab.dto.SellerDetailResponseDto;
import dev.harakki.shiftlab.dto.SellerSummaryResponseDto;
import dev.harakki.shiftlab.dto.SellerUpdateDto;
import dev.harakki.shiftlab.exception.EntityNotFoundException;
import dev.harakki.shiftlab.mapper.SellerMapper;
import dev.harakki.shiftlab.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerMapper sellerMapper;

    @InjectMocks
    private SellerService sellerService;

    @Test
    void findSellerById_shouldReturnSeller_whenExists() {
        Seller seller = mock(Seller.class);
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        Optional<Seller> result = sellerService.findSellerById(1L);

        assertTrue(result.isPresent());
        assertSame(seller, result.get());
        verify(sellerRepository).findById(1L);
    }

    @Test
    void findSellerById_shouldReturnEmpty_whenNotExists() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Seller> result = sellerService.findSellerById(1L);

        assertTrue(result.isEmpty());
        verify(sellerRepository).findById(1L);
    }

    @Test
    void getAll_shouldMapPageToDto() {
        Seller seller1 = mock(Seller.class);
        Seller seller2 = mock(Seller.class);

        when(sellerRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(seller1, seller2)));

        when(sellerMapper.toSellerSummaryResponseDto(seller1))
                .thenReturn(new SellerSummaryResponseDto(1L, "John Not Doe"));
        when(sellerMapper.toSellerSummaryResponseDto(seller2))
                .thenReturn(new SellerSummaryResponseDto(2L, "Alice"));

        var pageable = PageRequest.of(0, 10);

        var result = sellerService.getAll(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("John Not Doe", result.getContent().get(0).name());
        assertEquals("Alice", result.getContent().get(1).name());
        verify(sellerRepository).findAll(pageable);
    }

    @Test
    void getAll_shouldReturnEmptyPage_whenNoSellers() {
        Pageable pageable = PageRequest.of(0, 10);
        when(sellerRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        var result = sellerService.getAll(pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(sellerRepository).findAll(pageable);
    }

    @Test
    void get_shouldReturnSellerDetail_whenExists() {
        Seller seller = mock(Seller.class);
        var dto = new SellerDetailResponseDto(1L, "John", "contact", null);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerMapper.toSellerDetailResponseDto(seller)).thenReturn(dto);

        var result = sellerService.get(1L);

        assertEquals(1L, result.id());
        assertEquals("John", result.name());
        verify(sellerRepository).findById(1L);
        verify(sellerMapper).toSellerDetailResponseDto(seller);
    }

    @Test
    void get_shouldThrow_whenSellerMissing() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sellerService.get(1L));

        verify(sellerRepository).findById(1L);
        verifyNoInteractions(sellerMapper);
    }

    @Test
    void create_shouldSaveAndReturnDetail() {
        var request = new SellerCreateDto("John", "contact");
        Seller mapped = mock(Seller.class);
        Seller saved = mock(Seller.class);
        var dto = new SellerDetailResponseDto(1L, "John", "contact", null);

        when(sellerMapper.toEntity(request)).thenReturn(mapped);
        when(sellerRepository.save(mapped)).thenReturn(saved);
        when(sellerMapper.toSellerDetailResponseDto(saved)).thenReturn(dto);

        var result = sellerService.create(request);

        assertEquals(1L, result.id());
        assertEquals("John", result.name());
        verify(sellerMapper).toEntity(request);
        verify(sellerRepository).save(mapped);
        verify(sellerMapper).toSellerDetailResponseDto(saved);
    }

    @Test
    void update_shouldSaveUpdatedSeller_whenExists() {
        var request = new SellerUpdateDto("Updated", "new contact");
        Seller existing = mock(Seller.class);
        Seller updated = mock(Seller.class);
        Seller saved = mock(Seller.class);
        var dto = new SellerDetailResponseDto(1L, "Updated", "new contact", null);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(sellerMapper.partialUpdate(request, existing)).thenReturn(updated);
        when(sellerRepository.save(updated)).thenReturn(saved);
        when(sellerMapper.toSellerDetailResponseDto(saved)).thenReturn(dto);

        var result = sellerService.update(1L, request);

        assertEquals("Updated", result.name());
        assertEquals("new contact", result.contactInfo());
        verify(sellerRepository).findById(1L);
        verify(sellerMapper).partialUpdate(request, existing);
        verify(sellerRepository).save(updated);
    }

    @Test
    void update_shouldThrow_whenSellerMissing() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> sellerService.update(1L, new SellerUpdateDto("Updated", "contact")));

        verify(sellerRepository).findById(1L);
        verifyNoMoreInteractions(sellerMapper);
    }

    @Test
    void delete_shouldDelete_whenSellerExists() {
        when(sellerRepository.existsById(1L)).thenReturn(true);

        sellerService.delete(1L);

        verify(sellerRepository).existsById(1L);
        verify(sellerRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenSellerMissing() {
        when(sellerRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> sellerService.delete(1L));

        verify(sellerRepository).existsById(1L);
        verify(sellerRepository, never()).deleteById(any());
    }

}
